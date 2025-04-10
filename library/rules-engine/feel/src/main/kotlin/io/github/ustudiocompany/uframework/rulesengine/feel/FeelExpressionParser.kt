package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import org.camunda.feel.api.EvaluationResult
import org.camunda.feel.api.FeelEngineApi
import org.camunda.feel.api.FeelEngineBuilder
import org.camunda.feel.syntaxtree.ParsedExpression

public fun feelExpressionParser(configuration: FeelExpressionParserConfiguration): ExpressionParser =
    FeelExpressionParser(configuration)

private class FeelExpressionParser(configuration: FeelExpressionParserConfiguration) : ExpressionParser {
    private val engine: FeelEngineApi = FeelEngineBuilder.forJava()
        .withFunctionProvider(configuration.functionProvider)
        .withCustomValueMapper(FeelValueMapper())
        .build()

    override fun parse(expression: String): ResultK<FeelExpression, ExpressionParser.Errors> {
        val result = engine.parseExpression(expression)
        return if (result.isSuccess)
            FeelExpressionInstance(text = expression, parsedExpression = result.parsedExpression()).asSuccess()
        else
            ExpressionParser.Errors.Parsing(expression = expression, message = result.failure().message())
                .asFailure()
    }

    private inner class FeelExpressionInstance(
        override val text: String,
        private val parsedExpression: ParsedExpression
    ) : FeelExpression {

        override fun evaluate(context: Context): ResultK<DataElement, FeelExpression.EvaluateError> {
            val evaluationResult = engine.evaluate(parsedExpression, context.convert())
            return if (evaluationResult.isSuccess) {
                val result = evaluationResult.result() as DataElement
                if (result is DataElement.Null)
                    evaluateError(evaluationResult.descriptionSuppressedFailures())
                else
                    result.asSuccess()
            } else
                evaluateError(evaluationResult.failure().message())
        }

        private fun Context.convert(): Map<String, DataElement> {
            val result = mutableMapOf<String, DataElement>()
            toMap.forEach { (source, value) ->
                result[source.get] = value
            }
            return result
        }

        private fun evaluateError(message: String) =
            FeelExpression.EvaluateError(expression = this, message = message).asFailure()

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
    }
}
