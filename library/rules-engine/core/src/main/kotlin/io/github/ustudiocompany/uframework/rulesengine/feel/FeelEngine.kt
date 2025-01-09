package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.fullDescription
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import org.camunda.feel.api.EvaluationResult
import org.camunda.feel.api.FeelEngineApi
import org.camunda.feel.api.FeelEngineBuilder
import org.camunda.feel.syntaxtree.ParsedExpression

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
        val evaluationResult = engine.evaluate(expression, context.convert())
        return if (evaluationResult.isSuccess) {
            val result = evaluationResult.result() as DataElement
            if (result is DataElement.Null)
                Errors.Evaluate(
                    expression = expression.text(),
                    message = evaluationResult.descriptionSuppressedFailures()
                ).asFailure()
            else
                result.asSuccess()
        } else
            Errors.Evaluate(
                expression = expression.text(),
                message = evaluationResult.failure().message()
            ).asFailure()
    }

    private fun EvaluationResult.descriptionSuppressedFailures(): String {
        var failures = this.suppressedFailures()
        if (failures.isEmpty()) return ""
        return buildString {
            while (!failures.isEmpty()) {
                append(failures.head())
                failures = failures.tail()
            }
        }
    }

    private fun Map<Source, DataElement>.convert(): Map<String, DataElement> {
        val result = mutableMapOf<String, DataElement>()
        forEach { (source, value) ->
            result[source.get] = value
        }
        return result
    }

    public sealed class Errors : Failure {
        override fun toString(): String = this.fullDescription()

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
