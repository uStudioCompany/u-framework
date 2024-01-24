package io.github.ustudiocompany.uframework.saga.error

import io.github.ustudiocompany.uframework.failure.Failure

public sealed interface SagaPublisherErrors : SagaErrors {
    override val domain: String
        get() = "SAGA.PUBLISHER"

    public class CommandPublishing(cause: Failure) : SagaPublisherErrors {
        override val number: String = "1"
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val description: String = "An error to publishing command."
        override val kind: Failure.Kind
            get() = Failure.Kind.INCIDENT
    }
}
