package io.github.ustudiocompany.uframework.eventsourcing.store.model

import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Aggregate
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revisions

public data class TestAggregate(
    override val revisions: Revisions,
    public val entity: TestEntity
) : Aggregate<TestEntityId>() {
    override val id: TestEntityId
        get() = entity.id
}
