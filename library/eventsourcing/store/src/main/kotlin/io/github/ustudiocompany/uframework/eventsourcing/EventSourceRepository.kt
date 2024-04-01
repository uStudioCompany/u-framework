package io.github.ustudiocompany.uframework.eventsourcing

import io.github.airflux.functional.Result
import io.github.airflux.functional.ResultWith
import io.github.airflux.functional.mapError
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Aggregate
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.AggregateFactory
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revision
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.eventsourcing.event.EventName
import io.github.ustudiocompany.uframework.eventsourcing.store.event.EventStore
import io.github.ustudiocompany.uframework.eventsourcing.store.snapshot.SnapshotStore
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.retry.RetryScope

public class EventSourceRepository<AGGREGATE, ID, EVENT, NAME>(
    private val snapshotStore: SnapshotStore<AGGREGATE, ID>,
    private val eventStore: EventStore<EVENT, ID, NAME>,
    private val factory: AggregateFactory<AGGREGATE, ID, EVENT, NAME>
)
    where AGGREGATE : Aggregate<ID>,
          ID : EntityId,
          EVENT : Event<ID, NAME>,
          NAME : EventName {

    public fun loadAggregate(
        id: ID,
        maxCount: Int,
        retryScope: RetryScope
    ): Result<AGGREGATE?, EventSourceRepositoryErrors> =
        ResultWith {
            val (snapshot) = loadSnapshot(id, retryScope)

            val initialRevision = snapshot?.revisions?.current?.next() ?: Revision.initial
            var (events) = loadEvents(id, initialRevision, maxCount, retryScope)
            var aggregate = if (snapshot != null)
                snapshot.apply(events).bind()
            else
                events.replay().bind() ?: return@ResultWith Result.asNull

            while (true) {
                val revision: Revision = aggregate.revisions.current.next()
                events = loadEvents(id, revision, maxCount, retryScope).bind()
                if (events.isEmpty()) break
                aggregate = aggregate.apply(events).bind()
            }

            aggregate.success()
        }

    public fun loadEvent(
        id: ID,
        revision: Revision,
        retryScope: RetryScope
    ): Result<EVENT, EventSourceRepositoryErrors.Event> =
        eventStore.loadEvent(id, revision, retryScope)

    public fun saveEvent(
        event: EVENT,
        retryScope: RetryScope
    ): Result<Boolean, EventSourceRepositoryErrors.Event> =
        eventStore.saveEvent(event, retryScope)

    public fun saveSnapshot(
        aggregate: AGGREGATE,
        retryScope: RetryScope
    ): Result<Boolean, EventSourceRepositoryErrors.Snapshot> =
        snapshotStore.saveSnapshot(aggregate, retryScope)

    private fun loadSnapshot(
        id: ID,
        retryScope: RetryScope
    ): Result<AGGREGATE?, EventSourceRepositoryErrors.Snapshot> =
        snapshotStore.loadSnapshot(id, retryScope)

    private fun loadEvents(
        id: ID,
        revision: Revision,
        maxCount: Int,
        retryScope: RetryScope
    ): Result<List<EVENT>, EventSourceRepositoryErrors.Event> =
        eventStore.loadEvents(id, revision, maxCount, retryScope)

    private fun List<EVENT>.replay(): Result<AGGREGATE?, EventSourceRepositoryErrors.Aggregate> =
        iterator().replay(initial = { event -> factory.apply(null, event) }, factory = factory)

    private fun AGGREGATE.apply(events: List<EVENT>): Result<AGGREGATE, EventSourceRepositoryErrors.Aggregate.Create> =
        events.iterator().replay(initial = this, factory = factory)

    private fun Iterator<EVENT>.replay(
        initial: (EVENT) -> Result<AGGREGATE, Failure>,
        factory: AggregateFactory<AGGREGATE, ID, EVENT, NAME>
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
        factory: AggregateFactory<AGGREGATE, ID, EVENT, NAME>
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
