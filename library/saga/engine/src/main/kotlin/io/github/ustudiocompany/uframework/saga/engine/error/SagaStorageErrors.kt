package io.github.ustudiocompany.uframework.saga.engine.error

import io.github.ustudiocompany.uframework.failure.Cause
import io.github.ustudiocompany.uframework.failure.Failure

public sealed interface SagaStorageErrors : SagaErrors {

    public class Storage(cause: Failure) : SagaStorageErrors {
        override val code: String = PREFIX + "1"
        override val cause: Cause = Cause.Failure(cause)
        override val description: String = "An error to the Saga storage."
    }

    private companion object {
        private const val PREFIX = "SAGA-STORAGE-"
    }
}
