package io.github.ustudiocompany.uframework.rulesengine.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
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

        override fun evaluate(envVars: EnvVars, context: Context): ResultK<JsonElement, FeelExpression.EvaluateError> {
            val variables = variables(envVars, context)
            val evaluationResult = engine.evaluate(parsedExpression, variables)
            return if (evaluationResult.isSuccess) {
                val result = evaluationResult.result() as JsonElement
                if (result is JsonElement.Null)
                    evaluateError(evaluationResult.descriptionSuppressedFailures())
                else
                    result.asSuccess()
            } else
                evaluateError(evaluationResult.failure().message())
        }

        private fun variables(envVars: EnvVars, context: Context): Map<String, JsonElement> =
            if (envVars.isEmpty() && context.isEmpty())
                emptyMap()
            else
                mutableMapOf<String, JsonElement>()
                    .apply {
                        context.toMap(this)
                        envVars.toMap(this)
                    }

        private fun Context.toMap(destination: MutableMap<String, JsonElement>) {
            if (isNotEmpty())
                forEach { (source, value) ->
                    destination[source.get] = value
                }
        }

        private fun EnvVars.toMap(destination: MutableMap<String, JsonElement>) {
            if (isNotEmpty())
                forEach { (envVarName, value) ->
                    destination[envVarName.get] = value
                }
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
