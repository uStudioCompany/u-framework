package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.failure.Failure

public fun interface AggregateFactory<AGGREGATE, ID, EVENT>
    where AGGREGATE : Aggregate<ID>,
          ID : EntityId,
          EVENT : Event<ID> {

    public fun apply(aggregate: AGGREGATE?, event: EVENT): Result<AGGREGATE, Failure>
}
