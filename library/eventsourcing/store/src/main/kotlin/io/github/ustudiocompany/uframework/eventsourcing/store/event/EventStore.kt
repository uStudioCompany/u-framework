package io.github.ustudiocompany.uframework.eventsourcing.store.event

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.failure.Failure

public interface EventStore<EVENT, ID>
    where EVENT : Event<ID>,
          ID : EntityId {

    public fun loadEvents(aggregateId: ID, revision: Revision, maxCount: Int): ResultK<List<EVENT>, Failure>
    public fun loadEvent(aggregateId: ID, revision: Revision): ResultK<EVENT?, Failure>
    public fun saveEvent(event: EVENT): ResultK<Boolean, Failure>
}
