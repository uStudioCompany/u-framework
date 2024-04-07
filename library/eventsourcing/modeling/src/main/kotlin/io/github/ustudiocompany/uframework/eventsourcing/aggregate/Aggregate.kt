package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId

public interface Aggregate<out ID>
    where ID : EntityId {
    public val id: ID
    public val history: History
}
