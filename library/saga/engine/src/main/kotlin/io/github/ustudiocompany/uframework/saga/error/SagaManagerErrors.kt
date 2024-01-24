package io.github.ustudiocompany.uframework.saga.error

import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.saga.SagaLabel
import io.github.ustudiocompany.uframework.saga.error.SagaErrors.Companion.CORRELATION_ID_DETAIL_KEY
import io.github.ustudiocompany.uframework.saga.error.SagaErrors.Companion.MESSAGE_ACTION_DETAIL_KEY
import io.github.ustudiocompany.uframework.saga.error.SagaErrors.Companion.MESSAGE_VERSION_DETAIL_KEY
import io.github.ustudiocompany.uframework.saga.error.SagaErrors.Companion.SAGA_LABEL_DETAIL_KEY
import io.github.ustudiocompany.uframework.saga.message.header.type.CorrelationId

public sealed class SagaManagerErrors : SagaErrors {
    override val domain: String
        get() = "SAGA.MANAGER"

    /**
     * SI-1
     */
    public class SagaInstanceNotfound(public val correlationId: CorrelationId) : SagaManagerErrors() {
        override val number: String = "1"
        override val details: Failure.Details = Failure.Details.of(
            CORRELATION_ID_DETAIL_KEY to correlationId.get
        )
        override val kind: Failure.Kind
            get() = Failure.Kind.ERROR
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
        override val number: String = "2"
        override val details: Failure.Details = Failure.Details.of(
            CORRELATION_ID_DETAIL_KEY to correlationId.get,
            MESSAGE_ACTION_DETAIL_KEY to name.name,
            MESSAGE_VERSION_DETAIL_KEY to version.toString()
        )
        override val kind: Failure.Kind
            get() = Failure.Kind.ERROR
    }

    public class SagaForSagaInstanceNotFound(
        public val correlationId: CorrelationId,
        public val label: SagaLabel
    ) : SagaManagerErrors() {
        override val number: String = "3"
        override val details: Failure.Details = Failure.Details.of(
            CORRELATION_ID_DETAIL_KEY to correlationId.get,
            SAGA_LABEL_DETAIL_KEY to label.get
        )
        override val kind: Failure.Kind
            get() = Failure.Kind.ERROR
    }
}
