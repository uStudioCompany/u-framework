package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import org.camunda.feel.api.FeelEngineApi
import org.camunda.feel.api.FeelEngineBuilder
import org.camunda.feel.syntaxtree.ParsedExpression
import java.math.BigDecimal

public class FeelEngine(configuration: FeelEngineConfiguration) {
    private val engine: FeelEngineApi = FeelEngineBuilder.forJava()
        .withFunctionProvider(configuration.functionProvider)
        .withCustomValueMapper(FeelValueMapper())
        .build()

    public fun parse(expression: String): ResultK<ParsedExpression, Errors> {
        val result = engine.parseExpression(expression)
        return if (result.isSuccess)
            result.parsedExpression().asSuccess()
        else
            Errors.Parsing(expression = expression, message = result.failure().message()).asFailure()
    }

    public fun evaluate(
        expression: ParsedExpression,
        context: Map<Source, DataElement>
    ): ResultK<DataElement, Errors> {
        val evaluationResult = engine.evaluate(expression, context.toVariables())
        return if (evaluationResult.isSuccess) {
            val result = evaluationResult.result() as FeelExpressionValue
            if (result is FeelExpressionValue.Null)
                Errors.Evaluate(expression = expression.text()).asFailure()
            else
                result.asDataElement().asSuccess()
        } else
            Errors.Evaluate(
                expression = expression.text(),
                message = evaluationResult.failure().message()
            ).asFailure()
    }

    private fun Map<Source, DataElement>.toVariables(): Map<String, FeelExpressionValue> {
        val result = mutableMapOf<String, FeelExpressionValue>()
        forEach { (source, value) ->
            result[source.get] = value.toFeelValue()
        }
        return result
    }

    private fun DataElement.toFeelValue(): FeelExpressionValue = when (this) {
        is DataElement.Null -> FeelExpressionValue.Null
        is DataElement.Text -> FeelExpressionValue.Text(value = this.get)
        is DataElement.Decimal -> FeelExpressionValue.Decimal(value = this.get)
        is DataElement.Bool -> FeelExpressionValue.Bool(value = this.get)
        is DataElement.Array -> this.toFeelValue()
        is DataElement.Struct -> this.toFeelValue()
    }

    private fun DataElement.Array.toFeelValue(): FeelExpressionValue.Array =
        FeelExpressionValue.Array(items = this.map { it.toFeelValue() })

    private fun DataElement.Struct.toFeelValue(): FeelExpressionValue.Struct =
        FeelExpressionValue.Struct(properties = this.mapValues { it.value.toFeelValue() })

    private fun FeelExpressionValue.asDataElement(): DataElement = when (this) {
        is FeelExpressionValue.Null -> DataElement.Null
        is FeelExpressionValue.Text -> DataElement.Text(get = this.value)
        is FeelExpressionValue.Decimal -> DataElement.Decimal(get = BigDecimal(this.value.toString()))
        is FeelExpressionValue.Bool -> DataElement.Bool(get = this.value)
        is FeelExpressionValue.Array -> this.asArray()
        is FeelExpressionValue.Struct -> this.asStruct()
    }

    private fun FeelExpressionValue.Array.asArray(): DataElement.Array {
        val result = mutableListOf<DataElement>()
        items.forEach {
            result.add(it.asDataElement())
        }
        return DataElement.Array(result)
    }

    private fun FeelExpressionValue.Struct.asStruct(): DataElement {
        val result = mutableMapOf<String, DataElement>()
        properties.forEach {
            val key = it.key.toString()
            val value = it.value.asDataElement()
            result[key] = value
        }
        return DataElement.Struct(result)
    }

    public sealed class Errors : Failure {
        override fun toString(): String = this.joinDescriptions()

        public class Parsing(public val expression: String, public val message: String) : Errors() {
            override val code: String = PREFIX + "PARSING-EXPRESSION"
            override val description: String = "The error of parsing expression: `$expression`. $message"
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to expression
            )
        }

        public class Evaluate(expression: String, message: String? = null) : Errors() {
            override val code: String = PREFIX + "EVALUATE-EXPRESSION"
            override val description: String = "The error of evaluating expression: `$expression`" +
                if (message != null) "($message)." else ""
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to expression
            )
        }

        private companion object {
            private const val PREFIX = "FEEL-ENGINE-"
            private const val DETAILS_KEY_PATH = "feel-expression-body"
        }
    }
}
