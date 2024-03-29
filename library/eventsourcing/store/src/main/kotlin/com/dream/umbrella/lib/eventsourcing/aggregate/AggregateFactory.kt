package com.dream.umbrella.lib.eventsourcing.aggregate

import com.dream.umbrella.lib.eventsourcing.entity.EntityId
import com.dream.umbrella.lib.eventsourcing.event.Event
import com.dream.umbrella.lib.eventsourcing.event.EventName
import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure

public fun interface AggregateFactory<AGGREGATE, ID, EVENT, NAME>
    where AGGREGATE : Aggregate<ID>,
          ID : EntityId,
          EVENT : Event<ID, NAME>,
          NAME : EventName {

    public fun apply(aggregate: AGGREGATE?, event: EVENT): Result<AGGREGATE, Failure>
}
