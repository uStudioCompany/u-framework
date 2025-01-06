package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure

public class FeelExpressionError(cause: Failure) : RuleEngineError {
    override val code: String = "RULES-ENGINE-FEEL-EXPRESSION"
    override val description: String = "The error of evaluating the feel expression."
    override val cause: Failure.Cause = Failure.Cause.Failure(cause)
}
