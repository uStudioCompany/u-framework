package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.eventsourcing.event.EventName
import io.github.ustudiocompany.uframework.failure.Failure

public fun interface AggregateFactory<AGGREGATE, ID, EVENT, NAME>
    where AGGREGATE : Aggregate<ID>,
          ID : EntityId,
          EVENT : Event<ID, NAME>,
          NAME : EventName {

    public fun apply(aggregate: AGGREGATE?, event: EVENT): Result<AGGREGATE, Failure>
}
