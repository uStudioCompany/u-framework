package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure

public sealed interface FeelExpressionError : RuleEngineError {

    public class Evaluate(cause: Failure) : FeelExpressionError {
        override val code: String = PREFIX + "1"
        override val description: String = "The error of evaluating the expression."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "RULES-ENGINE-FEEL-EXPRESSION-"
    }
}
