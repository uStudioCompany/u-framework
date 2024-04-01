package io.github.ustudiocompany.uframework.eventsourcing.store.event

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revision
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.eventsourcing.event.EventName
import io.github.ustudiocompany.uframework.failure.Failure

public interface EventStore<EVENT, ID, NAME>
    where EVENT : Event<ID, NAME>,
          ID : EntityId,
          NAME : EventName {

    public fun loadEvents(id: ID, revision: Revision, maxCount: Int): Result<List<EVENT>, Failure>
    public fun loadEvent(id: ID, revision: Revision): Result<EVENT?, Failure>
    public fun saveEvent(event: EVENT): Result<Boolean, Failure>
}
