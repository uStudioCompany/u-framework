package io.github.ustudiocompany.uframework.messaging.dispatcher

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.router.RouterErrors

public sealed class DispatcherErrors : Failure {

    public class Route(cause: RouterErrors) : DispatcherErrors() {
        override val code: String = PREFIX + "1"
        override val description: String = "The routing error."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class Handler(cause: Failure) : DispatcherErrors() {
        override val code: String = PREFIX + "2"
        override val description: String = "The message handling error."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    public class PostHandler(cause: Failure) : DispatcherErrors() {
        override val code: String = PREFIX + "3"
        override val description: String = "The response post handling error."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DISPATCHER-"
    }
}
