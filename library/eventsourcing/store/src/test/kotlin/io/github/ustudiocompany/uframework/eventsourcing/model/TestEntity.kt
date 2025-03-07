package io.github.ustudiocompany.uframework.eventsourcing.model

import io.github.ustudiocompany.uframework.eventsourcing.entity.Entity
import io.github.ustudiocompany.uframework.eventsourcing.event.TestEvent

internal data class TestEntity(
    override val id: TestEntityId,
    val title: String?,
    val description: String?
) : Entity<TestEntityId>

internal fun TestEntity.applyEvent(event: TestEvent.Updated): TestEntity =
    TestEntity(
        id = id,
        title = event.data.title ?: title,
        description = event.data.description ?: description
    )
