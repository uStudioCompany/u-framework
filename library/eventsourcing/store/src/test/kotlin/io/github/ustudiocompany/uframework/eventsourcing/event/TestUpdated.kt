package io.github.ustudiocompany.uframework.eventsourcing.event

import io.github.ustudiocompany.uframework.eventsourcing.model.TestEntityId

public data class TestUpdated(
    public val id: TestEntityId,
    public val title: String?,
    public val description: String?
)
