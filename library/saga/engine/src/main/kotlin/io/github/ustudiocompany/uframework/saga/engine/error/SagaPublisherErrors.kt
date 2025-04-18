package io.github.ustudiocompany.uframework.saga.engine.error

import io.github.ustudiocompany.uframework.failure.Failure

public sealed interface SagaPublisherErrors : SagaErrors {

    public class CommandPublishing(cause: Failure) : SagaPublisherErrors {
        override val code: String = PREFIX + "1"
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val description: String = "An error to publishing command."
    }

    private companion object {
        private const val PREFIX = "PUBLISHER-"
    }
}
