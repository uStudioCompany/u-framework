package io.github.ustudiocompany.uframework.eventsourcing.event

import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

public interface Event<out ID, out NAME>
    where ID : EntityId,
          NAME : EventName {

    public val correlationId: CorrelationId
    public val aggregateId: ID
    public val messageId: MessageId
    public val revision: Revision
    public val name: NAME
}
