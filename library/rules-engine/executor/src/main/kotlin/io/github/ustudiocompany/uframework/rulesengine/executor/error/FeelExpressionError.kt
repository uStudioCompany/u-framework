package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Cause
import io.github.ustudiocompany.uframework.failure.Failure

public class FeelExpressionError(cause: Failure) : RuleEngineError {
    override val code: String = "RULES-ENGINE-FEEL-EXPRESSION-1"
    override val description: String = "The error of evaluating the feel expression."
    override val cause: Cause = Cause.Failure(cause)
}
