package io.github.ustudiocompany.uframework.eventsourcing.model

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Aggregate
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.History
import io.github.ustudiocompany.uframework.eventsourcing.event.TestEvent
import io.github.ustudiocompany.uframework.failure.Failure

internal data class TestAggregate(
    override val history: History,
    val entity: TestEntity
) : Aggregate<TestEntityId> {

    override val id: TestEntityId
        get() = entity.id

    companion object;
}

internal fun TestAggregate.Companion.applyEvent(event: TestEvent.Registered): ResultK<TestAggregate, Failure> = result {
    TestAggregate(
        history = History.of(event.revision, event.messageId).bind(),
        entity = TestEntity(
            id = event.data.id,
            title = event.data.title,
            description = event.data.description
        )
    )
}

internal fun TestAggregate.applyEvent(event: TestEvent.Updated): ResultK<TestAggregate, Failure> =
    result {
        TestAggregate(
            history = history.add(event.revision, event.messageId).bind(),
            entity = entity.applyEvent(event)
        )
    }
