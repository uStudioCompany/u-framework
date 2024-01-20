package io.github.ustudiocompany.uframework.saga.error

import io.github.ustudiocompany.uframework.failure.Failure

public sealed interface SagaErrors : Failure {

    public companion object {
        public const val CORRELATION_ID_DETAIL_KEY: String = "correlation-id"
        public const val MESSAGE_ACTION_DETAIL_KEY: String = "message-action"
        public const val MESSAGE_VERSION_DETAIL_KEY: String = "message-version"
        public const val MESSAGE_ID_DETAIL_KEY: String = "message-id"
        public const val SAGA_LABEL_DETAIL_KEY: String = "saga-label"
        public const val STEP_INDEX_DETAIL_KEY: String = "step-index"
        public const val STEP_LABEL_DETAIL_KEY: String = "step-label"
    }
}
