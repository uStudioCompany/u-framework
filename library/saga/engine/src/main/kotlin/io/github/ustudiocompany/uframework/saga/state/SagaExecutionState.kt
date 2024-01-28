package io.github.ustudiocompany.uframework.saga.state

import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.messaging.message.MessageRoutingKey
import io.github.ustudiocompany.uframework.utils.EnumElementProvider

public sealed interface SagaExecutionState<DATA> {
    public val routingKey: MessageRoutingKey
    public val correlationId: CorrelationId
    public val messageId: MessageId

    public class Initialization<DATA>(
        override val routingKey: MessageRoutingKey,
        override val correlationId: CorrelationId,
        override val messageId: MessageId,
        public val data: DATA
    ) : SagaExecutionState<DATA>

    public data class Continuation<DATA>(
        override val routingKey: MessageRoutingKey,
        override val correlationId: CorrelationId,
        override val messageId: MessageId,
        public val direction: Direction,
        public val status: Status,
        public val history: StepHistory,
        public val data: DATA
    ) : SagaExecutionState<DATA>

    public data class Resume<DATA>(
        override val routingKey: MessageRoutingKey,
        override val correlationId: CorrelationId,
        override val messageId: MessageId,
        public val direction: Direction,
        public val status: Status,
        public val history: StepHistory,
        public val data: DATA
    ) : SagaExecutionState<DATA>

    public enum class Status(override val key: String) : EnumElementProvider.Key {
        ACTIVE(key = "active"),
        COMPLETED_ROLLBACK(key = "completed-rollback"),
        COMPLETED_SUCCESSFULLY(key = "completed-successfully"),
        STOP(key = "stop"),
        ;

        public companion object : EnumElementProvider<Status>(info())
    }

    public enum class Direction(override val key: String) : EnumElementProvider.Key {
        PROCESSING("processing"),
        COMPENSATION("compensation");

        public companion object : EnumElementProvider<Direction>(info())
    }
}
