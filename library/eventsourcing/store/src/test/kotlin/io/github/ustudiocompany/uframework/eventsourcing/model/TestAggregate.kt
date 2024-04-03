package io.github.ustudiocompany.uframework.eventsourcing.model

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Aggregate
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revisions
import io.github.ustudiocompany.uframework.eventsourcing.event.TestEvent
import io.github.ustudiocompany.uframework.failure.Failure

public data class TestAggregate(
    override val revisions: Revisions,
    public val entity: TestEntity
) : Aggregate<TestEntityId>() {
    override val id: TestEntityId
        get() = entity.id

    public companion object;
}

public fun TestAggregate.Companion.applyEvent(event: TestEvent.Registered): TestAggregate = TestAggregate(
    revisions = Revisions.initial,
    entity = TestEntity(
        id = event.data.id,
        title = event.data.title,
        description = event.data.description
    )
)

public fun TestAggregate.applyEvent(event: TestEvent.Updated): Result<TestAggregate, Failure> =
    Result {
        TestAggregate(
            revisions = revisions.apply(event).bind(),
            entity = entity.applyEvent(event)
        )
    }
