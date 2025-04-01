package io.github.ustudiocompany.uframework.saga.engine.error

import io.github.ustudiocompany.uframework.failure.Details
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.saga.core.SagaLabel

public sealed class SagaManagerErrors : SagaErrors {

    /**
     * SI-1
     */
    public class SagaInstanceNotFound(public val correlationId: CorrelationId) : SagaManagerErrors() {
        override val code: String = PREFIX + "1"
        override val details: Details = Details.of(
            CORRELATION_ID_DETAIL_KEY to correlationId.get
        )
    }

    /**
     * S-1
     * Не знайдена версія Saga яка потрібна для подальшого виконная SagaInstance
     */
    public class SagaForCommandNotFound(
        public val correlationId: CorrelationId,
        public val name: MessageName,
        public val version: MessageVersion
    ) : SagaManagerErrors() {
        override val code: String = PREFIX + "2"
        override val details: Details = Details.of(
            CORRELATION_ID_DETAIL_KEY to correlationId.get,
            MESSAGE_ACTION_DETAIL_KEY to name.name,
            MESSAGE_VERSION_DETAIL_KEY to version.toString()
        )
    }

    public class SagaForSagaInstanceNotFound(
        public val correlationId: CorrelationId,
        public val label: SagaLabel
    ) : SagaManagerErrors() {
        override val code: String = PREFIX + "3"
        override val details: Details = Details.of(
            CORRELATION_ID_DETAIL_KEY to correlationId.get,
            SAGA_LABEL_DETAIL_KEY to label.get
        )
    }

    public companion object {
        private const val PREFIX = "SAGA-MANAGER-"
        public const val CORRELATION_ID_DETAIL_KEY: String = "correlation-id"
        public const val MESSAGE_ACTION_DETAIL_KEY: String = "message-action"
        public const val MESSAGE_VERSION_DETAIL_KEY: String = "message-version"
        public const val SAGA_LABEL_DETAIL_KEY: String = "saga-label"
    }
}
