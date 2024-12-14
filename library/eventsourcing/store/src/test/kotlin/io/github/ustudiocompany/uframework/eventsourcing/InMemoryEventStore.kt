package io.github.ustudiocompany.uframework.eventsourcing

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.eventsourcing.event.TestEvent
import io.github.ustudiocompany.uframework.eventsourcing.model.TestEntityId
import io.github.ustudiocompany.uframework.eventsourcing.store.event.EventStore
import io.github.ustudiocompany.uframework.failure.Failure

internal class InMemoryEventStore(
    private val data: MutableMap<TestEntityId, List<TestEvent>> = mutableMapOf()
) : EventStore<TestEvent, TestEntityId> {

    override fun loadEvents(
        aggregateId: TestEntityId,
        revision: Revision,
        maxCount: Int
    ): ResultK<List<TestEvent>, Failure> {
        val events = data[aggregateId] ?: return Success.asEmptyList
        val index = events.indexOfFirst { it.revision == revision }
        return if (index == -1)
            Success.asEmptyList
        else
            events.subList(index, events.size.coerceAtMost(index + maxCount)).asSuccess()
    }

    override fun loadEvent(aggregateId: TestEntityId, revision: Revision): ResultK<TestEvent?, Failure> {
        val events = data[aggregateId] ?: return Success.asNull
        return events.firstOrNull { it.revision == revision }.asSuccess()
    }

    override fun saveEvent(event: TestEvent): ResultK<Boolean, Failure> {
        fun List<TestEvent>.isPresent(event: TestEvent): Boolean = any { it.revision == event.revision }

        val aggregateId = event.aggregateId
        val updatedEvents = data[aggregateId]
            ?.let { events ->
                if (events.isPresent(event)) return Success.asFalse
                events + event
            }
            ?: listOf(event)

        data[aggregateId] = updatedEvents
        return Success.asTrue
    }
}
