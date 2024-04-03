package io.github.ustudiocompany.uframework.eventsourcing.model

import io.github.ustudiocompany.uframework.eventsourcing.entity.Entity
import io.github.ustudiocompany.uframework.eventsourcing.event.TestEvent

public data class TestEntity(
    override val id: TestEntityId,
    public val title: String?,
    public val description: String?
) : Entity<TestEntityId>

public fun TestEntity.applyEvent(event: TestEvent.Updated): TestEntity =
    TestEntity(
        id = id,
        title = event.data.title ?: title,
        description = event.data.description ?: description
    )
