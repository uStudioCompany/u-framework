package io.github.ustudiocompany.uframework.eventsourcing

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Aggregate
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.AggregateFactory
import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.eventsourcing.store.event.EventStore
import io.github.ustudiocompany.uframework.eventsourcing.store.snapshot.SnapshotStore
import io.github.ustudiocompany.uframework.failure.Failure

public class EventSourceRepository<AGGREGATE, ID, EVENT>(
    private val snapshotStore: SnapshotStore<AGGREGATE, ID>,
    private val eventStore: EventStore<EVENT, ID>,
    private val factory: AggregateFactory<AGGREGATE, ID, EVENT>
)
    where AGGREGATE : Aggregate<ID>,
          ID : EntityId,
          EVENT : Event<ID> {

    public fun loadAggregate(aggregateId: ID, maxCount: Int): ResultK<AGGREGATE?, EventSourceRepositoryErrors> =
        resultWith {
            val (snapshot) = loadSnapshot(aggregateId)

            val initialRevision = snapshot?.history?.revision?.next() ?: Revision.INITIAL
            var (events) = loadEvents(aggregateId, initialRevision, maxCount)
            var aggregate = if (snapshot != null)
                snapshot.apply(events).bind()
            else
                events.replay().bind() ?: return@resultWith Success.asNull

            while (true) {
                val revision: Revision = aggregate.history.revision.next()
                events = loadEvents(aggregateId, revision, maxCount).bind()
                if (events.isEmpty()) break
                aggregate = aggregate.apply(events).bind()
            }

            aggregate.asSuccess()
        }

    public fun loadEvent(aggregateId: ID, revision: Revision): ResultK<EVENT?, EventSourceRepositoryErrors.Event.Load> =
        eventStore.loadEvent(aggregateId, revision)
            .mapFailure { failure -> EventSourceRepositoryErrors.Event.Load(failure) }

    public fun saveEvent(event: EVENT): ResultK<Boolean, EventSourceRepositoryErrors.Event.Save> =
        eventStore.saveEvent(event)
            .mapFailure { failure -> EventSourceRepositoryErrors.Event.Save(failure) }

    public fun saveSnapshot(aggregate: AGGREGATE): ResultK<Boolean, EventSourceRepositoryErrors.Snapshot.Save> =
        snapshotStore.saveSnapshot(aggregate)
            .mapFailure { failure -> EventSourceRepositoryErrors.Snapshot.Save(failure) }

    private fun loadSnapshot(aggregateId: ID): ResultK<AGGREGATE?, EventSourceRepositoryErrors.Snapshot.Load> =
        snapshotStore.loadSnapshot(aggregateId)
            .mapFailure { failure -> EventSourceRepositoryErrors.Snapshot.Load(failure) }

    private fun loadEvents(
        aggregateId: ID,
        revision: Revision,
        maxCount: Int
    ): ResultK<List<EVENT>, EventSourceRepositoryErrors.Event.Load> =
        eventStore.loadEvents(aggregateId, revision, maxCount)
            .mapFailure { failure -> EventSourceRepositoryErrors.Event.Load(failure) }

    private fun List<EVENT>.replay(): ResultK<AGGREGATE?, EventSourceRepositoryErrors.Aggregate.Create> =
        iterator().replay(initial = { event -> factory.apply(null, event) }, factory = factory)

    private fun AGGREGATE.apply(events: List<EVENT>): ResultK<AGGREGATE, EventSourceRepositoryErrors.Aggregate.Create> =
        events.iterator().replay(initial = this, factory = factory)

    private fun Iterator<EVENT>.replay(
        initial: (EVENT) -> ResultK<AGGREGATE, Failure>,
        factory: AggregateFactory<AGGREGATE, ID, EVENT>
    ): ResultK<AGGREGATE?, EventSourceRepositoryErrors.Aggregate.Create> = resultWith {
        val events = this@replay
        if (!events.hasNext()) return Success.asNull
        val event = events.next()
        val (aggregate: AGGREGATE) = initial(event)
            .mapFailure { failure -> EventSourceRepositoryErrors.Aggregate.Create(failure) }

        events.replay(aggregate, factory)
    }

    private fun Iterator<EVENT>.replay(
        initial: AGGREGATE,
        factory: AggregateFactory<AGGREGATE, ID, EVENT>
    ): ResultK<AGGREGATE, EventSourceRepositoryErrors.Aggregate.Create> = result {
        var aggregate = initial
        val events = this@replay
        for (event in events) {
            aggregate = factory.apply(aggregate, event)
                .mapFailure { failure -> EventSourceRepositoryErrors.Aggregate.Create(failure) }
                .bind()
        }
        aggregate
    }
}
