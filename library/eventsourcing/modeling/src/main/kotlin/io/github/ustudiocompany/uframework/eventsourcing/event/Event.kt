package io.github.ustudiocompany.uframework.eventsourcing.event

import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

public interface Event<out ID>
    where ID : EntityId {

    public val aggregateId: ID
    public val revision: Revision
    public val messageId: MessageId
}
