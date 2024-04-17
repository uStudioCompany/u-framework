package io.github.ustudiocompany.uframework.eventsourcing

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.success
import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.eventsourcing.event.TestEvent
import io.github.ustudiocompany.uframework.eventsourcing.model.TestEntityId
import io.github.ustudiocompany.uframework.eventsourcing.store.event.EventStore
import io.github.ustudiocompany.uframework.failure.Failure

internal class InMemoryEventStore(
    private val data: MutableMap<TestEntityId, List<TestEvent>> = mutableMapOf()
) : EventStore<TestEvent, TestEntityId> {

    override fun loadEvents(aggregateId: TestEntityId, revision: Revision, maxCount: Int): Result<List<TestEvent>, Failure> {
        val events = data[aggregateId] ?: return Result.asEmptyList
        val index = events.indexOfFirst { it.revision == revision }
        return if (index == -1)
            Result.asEmptyList
        else
            events.subList(index, events.size.coerceAtMost(index + maxCount)).success()
    }

    override fun loadEvent(aggregateId: TestEntityId, revision: Revision): Result<TestEvent?, Failure> {
        val events = data[aggregateId] ?: return Result.asNull
        return events.firstOrNull { it.revision == revision }.success()
    }

    override fun saveEvent(event: TestEvent): Result<Boolean, Failure> {
        fun List<TestEvent>.isPresent(event: TestEvent): Boolean = any { it.revision == event.revision }

        val aggregateId = event.aggregateId
        val updatedEvents = data[aggregateId]
            ?.let { events ->
                if (events.isPresent(event)) return Result.asFalse
                events + event
            }
            ?: listOf(event)

        data[aggregateId] = updatedEvents
        return Result.asTrue
    }
}
