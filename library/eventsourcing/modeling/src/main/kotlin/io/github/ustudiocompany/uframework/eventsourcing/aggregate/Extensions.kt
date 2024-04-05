package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId

private const val NUMBER_EVENTS_FOR_CREATE_SNAPSHOT = 10
public fun <AGGREGATE, ID> AGGREGATE.needCreateSnapshot(): Boolean
    where AGGREGATE : Aggregate<ID>,
          ID : EntityId =
    (history.events.size % NUMBER_EVENTS_FOR_CREATE_SNAPSHOT) == 0
