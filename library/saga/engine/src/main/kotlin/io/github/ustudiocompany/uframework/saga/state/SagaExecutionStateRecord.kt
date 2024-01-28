package io.github.ustudiocompany.uframework.saga.state

import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.messaging.message.MessageRoutingKey
import io.github.ustudiocompany.uframework.saga.SagaLabel

public data class SagaExecutionStateRecord(
    public val routingKey: MessageRoutingKey,
    public val correlationId: CorrelationId,
    public val messageId: MessageId,
    public val label: SagaLabel,
    public val direction: SagaExecutionState.Direction,
    public val status: SagaExecutionState.Status,
    public val history: StepHistory,
    public val serializedData: SerializedData
)
