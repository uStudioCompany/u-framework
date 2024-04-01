package io.github.ustudiocompany.uframework.eventsourcing.store.model

import io.github.ustudiocompany.uframework.eventsourcing.entity.Entity

public data class TestEntity(
    override val id: TestEntityId,
    public val title: String?,
    public val description: String?
) : Entity<TestEntityId>
