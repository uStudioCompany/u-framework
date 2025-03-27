package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression

public sealed interface FeelExpressionError : RuleEngineError {

    /**
     * Represents an error that occurs during the evaluation of a FEEL (Friendly Enough Expression Language) expression.
     *
     * @param cause The root failure that caused the evaluation error.
     */
    public class Evaluate(cause: FeelExpression.EvaluateError) : FeelExpressionError {
        override val code: String = PREFIX + "1"
        override val description: String = "The error of evaluating the expression."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "RULES-ENGINE-FEEL-EXPRESSION-"
    }
}
