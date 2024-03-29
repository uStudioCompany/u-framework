package com.dream.umbrella.lib.eventsourcing.event

import com.dream.umbrella.lib.eventsourcing.aggregate.Revision
import com.dream.umbrella.lib.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

public abstract class Event<out ID, NAME>
    where ID : EntityId,
          NAME : EventName {

    public abstract val aggregateId: ID
    public abstract val commandId: MessageId
    public abstract val correlationId: CorrelationId
    public abstract val revision: Revision
    public abstract val name: NAME

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is Event<*, *> && this.aggregateId == other.aggregateId && this.commandId == other.commandId)

    override fun hashCode(): Int = commandId.hashCode()
}
