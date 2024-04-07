package io.github.ustudiocompany.uframework.eventsourcing.store.event

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.failure.Failure

public interface EventStore<EVENT, ID>
    where EVENT : Event<ID>,
          ID : EntityId {

    public fun loadEvents(aggregateId: ID, revision: Revision, maxCount: Int): Result<List<EVENT>, Failure>
    public fun loadEvent(aggregateId: ID, revision: Revision): Result<EVENT?, Failure>
    public fun saveEvent(event: EVENT): Result<Boolean, Failure>
}
