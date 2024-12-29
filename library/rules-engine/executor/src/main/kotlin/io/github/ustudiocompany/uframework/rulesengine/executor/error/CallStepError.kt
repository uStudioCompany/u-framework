package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure

public class CallStepError(cause: Failure) : RuleEngineError {
    override val code: String = "RULES-ENGINE-CALL-STEP"
    override val description: String = "Call error."
    override val cause: Failure.Cause = Failure.Cause.Failure(cause)
}
