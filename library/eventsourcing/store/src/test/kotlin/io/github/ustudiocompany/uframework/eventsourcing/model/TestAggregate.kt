package io.github.ustudiocompany.uframework.eventsourcing.model

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Aggregate
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revisions
import io.github.ustudiocompany.uframework.eventsourcing.event.TestEvent
import io.github.ustudiocompany.uframework.failure.Failure

public data class TestAggregate(
    override val revisions: Revisions,
    public val entity: TestEntity
) : Aggregate<TestEntityId> {

    override val id: TestEntityId
        get() = entity.id

    public companion object;
}

public fun TestAggregate.Companion.applyEvent(event: TestEvent.Registered): Result<TestAggregate, Failure> = Result {
    val (revisions) = Revisions.of(event.revision, event.commandId)
    TestAggregate(
        revisions = revisions,
        entity = TestEntity(
            id = event.data.id,
            title = event.data.title,
            description = event.data.description
        )
    )
}

public fun TestAggregate.applyEvent(event: TestEvent.Updated): Result<TestAggregate, Failure> =
    Result {
        val (newRevision) = revisions.add(event.revision, event.commandId)
        TestAggregate(
            revisions = newRevision,
            entity = entity.applyEvent(event)
        )
    }
