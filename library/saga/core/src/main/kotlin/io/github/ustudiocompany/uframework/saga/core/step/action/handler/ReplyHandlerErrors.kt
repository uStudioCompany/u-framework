package io.github.ustudiocompany.uframework.saga.core.step.action.handler

import io.github.ustudiocompany.uframework.failure.Cause
import io.github.ustudiocompany.uframework.failure.Failure

public sealed class ReplyHandlerErrors : Failure {

    /**
     * Відповідь не містить тіло (R-4)
     */
    public class ReplyBodyMissing : ReplyHandlerErrors() {
        override val code: String = PREFIX + "1"
        override val description: String = "The reply body is missing."
    }

    /**
     * Помилка десеріалізації тіла відповіді (R-5)
     */
    public class ReplyBodyDeserialization(cause: Failure) : ReplyHandlerErrors() {
        override val code: String = PREFIX + "2"
        override val description: String = "An error of deserialize the reply body."
        override val cause: Cause = Cause.Failure(cause)
    }

    /**
     * R-6
     */
    public class ReplyHandle(cause: Failure) : ReplyHandlerErrors() {
        override val code: String = PREFIX + "3"
        override val description: String = "An error of handle the reply."
        override val cause: Cause = Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "SAGA-REPLY-HANDLER-"
    }
}
