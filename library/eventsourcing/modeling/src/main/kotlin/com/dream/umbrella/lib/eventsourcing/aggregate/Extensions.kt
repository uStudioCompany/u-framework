package com.dream.umbrella.lib.eventsourcing.aggregate

import com.dream.umbrella.lib.eventsourcing.entity.EntityId

private const val NUMBER_EVENTS_FOR_CREATE_SNAPSHOT = 10
public fun <AGGREGATE, ID> AGGREGATE.needCreateSnapshot(): Boolean
    where AGGREGATE : Aggregate<ID>,
          ID : EntityId {
    val number = revisions.current.get - revisions.history.maxRevision().get
    return (number >= NUMBER_EVENTS_FOR_CREATE_SNAPSHOT)
}
