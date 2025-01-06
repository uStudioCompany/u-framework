package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.traverseTo
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import org.camunda.feel.api.FeelEngineApi
import org.camunda.feel.api.FeelEngineBuilder
import org.camunda.feel.syntaxtree.ParsedExpression
import java.math.BigDecimal

public class FeelEngine(configuration: FeelEngineConfiguration) {
    private val engine: FeelEngineApi = FeelEngineBuilder.forJava()
        .withFunctionProvider(configuration.functionProvider)
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
        variables: Map<String, DataElement> = emptyMap()
    ): ResultK<DataElement, Errors> {
        val evaluationResult = engine.evaluate(expression, variables)
        return if (evaluationResult.isSuccess) {
            val result = evaluationResult.result()
            if (result != null)
                result.asDataElement()
                    .mapFailure { Errors.Evaluate(expression = expression.text(), message = it) }
            else
                Errors.Evaluate(expression = expression.text()).asFailure()
        } else
            Errors.Evaluate(
                expression = expression.text(),
                message = evaluationResult.failure().message()
            ).asFailure()
    }

    private fun Any?.asDataElement(): ResultK<DataElement, String> = when (this) {
        null -> DataElement.Null.asSuccess()
        is String -> DataElement.Text(get = this).asSuccess()
        is Number -> DataElement.Decimal(get = BigDecimal(this.toString())).asSuccess()
        is Boolean -> DataElement.Bool(get = this).asSuccess()
        is java.util.AbstractList<*> -> this.asArray()
        is java.util.AbstractMap<*, *> -> this.asStruct()
        else -> "Unexpected result type of the evaluated expression: `$javaClass`".asFailure()
    }

    private fun java.util.AbstractList<*>.asArray(): ResultK<DataElement.Array, String> =
        traverseTo(mutableListOf<DataElement>()) { item -> item.asDataElement() }
            .map { DataElement.Array(it) }

    private fun java.util.AbstractMap<*, *>.asStruct(): ResultK<DataElement, String> {
        val result = mutableMapOf<String, DataElement>()
        forEach {
            val key = it.key.toString()
            val value = it.value.asDataElement().getOrForward { return it }
            result[key] = value
        }
        return DataElement.Struct(result).asSuccess()
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
