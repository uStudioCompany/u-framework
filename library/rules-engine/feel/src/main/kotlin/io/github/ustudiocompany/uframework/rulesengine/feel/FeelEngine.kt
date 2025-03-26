package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
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

    public fun parse(expression: String): ResultK<FeelExpression, Errors> {
        val result = engine.parseExpression(expression)
        return if (result.isSuccess)
            ExpressionInstance(result.parsedExpression()).asSuccess()
        else
            Errors.Parsing(expression = expression, message = result.failure().message()).asFailure()
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

    private inner class ExpressionInstance(
        private val parsedExpression: ParsedExpression
    ) : FeelExpression {

        override val value: String
            get() = parsedExpression.text()

        override fun evaluate(context: Map<Source, DataElement>): ResultK<DataElement, FeelExpression.Errors.Evaluate> {
            val evaluationResult = engine.evaluate(parsedExpression, context.convert())
            return if (evaluationResult.isSuccess) {
                val result = evaluationResult.result() as DataElement
                if (result is DataElement.Null)
                    FeelExpression.Errors.Evaluate(
                        expression = this,
                        message = evaluationResult.descriptionSuppressedFailures()
                    ).asFailure()
                else
                    result.asSuccess()
            } else
                FeelExpression.Errors.Evaluate(
                    expression = this,
                    message = evaluationResult.failure().message()
                ).asFailure()
        }
    }

    public sealed class Errors : Failure {

        public class Parsing(public val expression: String, message: String) : Errors() {
            override val code: String = PREFIX + "1"
            override val description: String = "The error of parsing expression: '$expression'. $message"
            override val details: Failure.Details = Failure.Details.of(
                DETAILS_KEY_PATH to expression
            )
        }

        private companion object {
            private const val PREFIX = "FEEL-ENGINE-"
            private const val DETAILS_KEY_PATH = "feel-expression"
        }
    }
}
