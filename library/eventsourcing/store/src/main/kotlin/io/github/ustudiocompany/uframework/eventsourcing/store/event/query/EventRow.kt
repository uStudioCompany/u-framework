package io.github.ustudiocompany.uframework.eventsourcing.store.event.query

import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revision
import io.github.ustudiocompany.uframework.eventsourcing.event.EventName
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

public class EventRow<NAME : EventName>(
    public val commandId: MessageId,
    public val correlationId: CorrelationId,
    public val name: NAME,
    public val revision: Revision,
    public val data: String
)
