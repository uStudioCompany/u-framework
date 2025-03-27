package io.github.ustudiocompany.uframework.rulesengine.executor.error

import io.github.ustudiocompany.uframework.failure.Failure

public sealed interface CallStepError : RuleEngineError {

    /**
     * Represents an error that occurs during an external call in the rule engine execution.
     *
     * @param cause The root failure that caused the call error.
     */
    public class Call(cause: Failure) : CallStepError {
        override val code: String = PREFIX + "1"
        override val description: String = "Call error."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "RULES-ENGINE-CALL-STEP-"
    }
}
