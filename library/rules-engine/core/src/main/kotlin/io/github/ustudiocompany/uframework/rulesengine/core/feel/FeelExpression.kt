package io.github.ustudiocompany.uframework.rulesengine.core.feel

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars

public interface FeelExpression {

    public val text: String

    public fun evaluate(envVars: EnvVars, context: Context): ResultK<JsonElement, EvaluateError>

    /**
     * Represents an evaluation error when processing a FEEL expression.
     *
     * @property expression The expression that caused the evaluation error.
     */
    public class EvaluateError(expression: FeelExpression, message: String? = null) : Failure {
        override val code: String = PREFIX + "1"
        override val description: String = "The error of evaluating expression: '${expression.text}'" +
            if (message != null) " ($message)." else "."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_EXPRESSION to expression.text
        )

        private companion object {
            private const val PREFIX = "EVALUATE-FEEL-EXPRESSION-"
            private const val DETAILS_KEY_EXPRESSION = "feel-expression"
        }
    }
}
