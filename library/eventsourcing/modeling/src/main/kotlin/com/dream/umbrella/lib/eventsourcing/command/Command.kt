package com.dream.umbrella.lib.eventsourcing.command

import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

public abstract class Command {
    public abstract val id: MessageId
    public abstract val correlationId: CorrelationId

    override fun equals(other: Any?): Boolean =
        this === other || (other is Command && this.id == other.id)

    override fun hashCode(): Int = id.hashCode()
}
