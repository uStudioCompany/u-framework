package io.github.ustudiocompany.uframework.saga.error

import io.github.ustudiocompany.uframework.failure.Failure

public sealed interface SagaPublisherErrors : SagaErrors {
    override val domain: String
        get() = "SAGA.PUBLISHER"

    public class CommandPublishing(cause: Failure) : SagaPublisherErrors {
        override val number: String = "2"
        override val cause: Failure.Cause = Failure.Cause.Error(cause)
        override val description: String = "An error to publishing command."
    }
}
