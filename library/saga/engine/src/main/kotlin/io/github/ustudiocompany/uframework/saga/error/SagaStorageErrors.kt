package io.github.ustudiocompany.uframework.saga.error

import io.github.ustudiocompany.uframework.failure.Failure

public sealed interface SagaStorageErrors : SagaErrors {
    override val domain: String
        get() = "SAGA.STORAGE"

    public class Storage(cause: Failure) : SagaStorageErrors {
        override val number: String = "1"
        override val cause: Failure.Cause = Failure.Cause.Error(cause)
        override val description: String = "An error to the Saga storage."
    }
}
