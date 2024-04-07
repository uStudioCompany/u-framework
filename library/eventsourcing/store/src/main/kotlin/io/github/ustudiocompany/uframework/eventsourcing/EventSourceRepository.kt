package io.github.ustudiocompany.uframework.eventsourcing

import io.github.airflux.functional.Result
import io.github.airflux.functional.ResultWith
import io.github.airflux.functional.mapError
import io.github.airflux.functional.success
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

    public fun loadAggregate(aggregateId: ID, maxCount: Int): Result<AGGREGATE?, EventSourceRepositoryErrors> =
        ResultWith {
            val (snapshot) = loadSnapshot(aggregateId)

            val initialRevision = snapshot?.history?.revision?.next() ?: Revision.initial
            var (events) = loadEvents(aggregateId, initialRevision, maxCount)
            var aggregate = if (snapshot != null)
                snapshot.apply(events).bind()
            else
                events.replay().bind() ?: return@ResultWith Result.asNull

            while (true) {
                val revision: Revision = aggregate.history.revision.next()
                events = loadEvents(aggregateId, revision, maxCount).bind()
                if (events.isEmpty()) break
                aggregate = aggregate.apply(events).bind()
            }

            aggregate.success()
        }

    public fun loadEvent(aggregateId: ID, revision: Revision): Result<EVENT?, EventSourceRepositoryErrors.Event.Load> =
        eventStore.loadEvent(aggregateId, revision)
            .mapError { failure -> EventSourceRepositoryErrors.Event.Load(failure) }

    public fun saveEvent(event: EVENT): Result<Boolean, EventSourceRepositoryErrors.Event.Save> =
        eventStore.saveEvent(event)
            .mapError { failure -> EventSourceRepositoryErrors.Event.Save(failure) }

    public fun saveSnapshot(aggregate: AGGREGATE): Result<Boolean, EventSourceRepositoryErrors.Snapshot.Save> =
        snapshotStore.saveSnapshot(aggregate)
            .mapError { failure -> EventSourceRepositoryErrors.Snapshot.Save(failure) }

    private fun loadSnapshot(aggregateId: ID): Result<AGGREGATE?, EventSourceRepositoryErrors.Snapshot.Load> =
        snapshotStore.loadSnapshot(aggregateId)
            .mapError { failure -> EventSourceRepositoryErrors.Snapshot.Load(failure) }

    private fun loadEvents(
        aggregateId: ID,
        revision: Revision,
        maxCount: Int
    ): Result<List<EVENT>, EventSourceRepositoryErrors.Event.Load> =
        eventStore.loadEvents(aggregateId, revision, maxCount)
            .mapError { failure -> EventSourceRepositoryErrors.Event.Load(failure) }

    private fun List<EVENT>.replay(): Result<AGGREGATE?, EventSourceRepositoryErrors.Aggregate.Create> =
        iterator().replay(initial = { event -> factory.apply(null, event) }, factory = factory)

    private fun AGGREGATE.apply(events: List<EVENT>): Result<AGGREGATE, EventSourceRepositoryErrors.Aggregate.Create> =
        events.iterator().replay(initial = this, factory = factory)

    private fun Iterator<EVENT>.replay(
        initial: (EVENT) -> Result<AGGREGATE, Failure>,
        factory: AggregateFactory<AGGREGATE, ID, EVENT>
    ): Result<AGGREGATE?, EventSourceRepositoryErrors.Aggregate.Create> = ResultWith {
        val events = this@replay
        if (!events.hasNext()) return Result.asNull
        val event = events.next()
        val (aggregate: AGGREGATE) = initial(event)
            .mapError { failure -> EventSourceRepositoryErrors.Aggregate.Create(failure) }

        events.replay(aggregate, factory)
    }

    private fun Iterator<EVENT>.replay(
        initial: AGGREGATE,
        factory: AggregateFactory<AGGREGATE, ID, EVENT>
    ): Result<AGGREGATE, EventSourceRepositoryErrors.Aggregate.Create> = Result {
        var aggregate = initial
        val events = this@replay
        for (event in events) {
            aggregate = factory.apply(aggregate, event)
                .mapError { failure -> EventSourceRepositoryErrors.Aggregate.Create(failure) }
                .bind()
        }
        aggregate
    }
}
