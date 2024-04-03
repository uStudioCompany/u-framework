package io.github.ustudiocompany.uframework.eventsourcing.event

import io.github.ustudiocompany.uframework.eventsourcing.model.TestEntityId

public data class TestRegistered(
    public val id: TestEntityId,
    public val title: String?,
    public val description: String?
)
