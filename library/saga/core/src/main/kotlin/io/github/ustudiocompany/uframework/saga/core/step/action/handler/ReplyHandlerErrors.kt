package io.github.ustudiocompany.uframework.saga.core.step.action.handler

import io.github.ustudiocompany.uframework.failure.Failure

public sealed class ReplyHandlerErrors : Failure {
    override val domain: String
        get() = "SAGA-REPLY-HANDLER"

    /**
     * Відповідь не містить тіло (R-4)
     */
    public class ReplyBodyMissing : ReplyHandlerErrors() {
        override val number: String = "1"
        override val description: String = "The reply body is missing."
    }

    /**
     * Помилка десеріалізації тіла відповіді (R-5)
     */
    public class ReplyBodyDeserialization(cause: Failure) : ReplyHandlerErrors() {
        override val number: String = "2"
        override val description: String = "An error of deserialize the reply body."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    /**
     * R-6
     */
    public class ReplyHandle(cause: Failure) : ReplyHandlerErrors() {
        override val number: String = "3"
        override val description: String = "An error of handle the reply."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }
}
