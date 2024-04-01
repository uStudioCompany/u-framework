package io.github.ustudiocompany.uframework.eventsourcing.store.event.model

import io.github.ustudiocompany.uframework.eventsourcing.store.model.TestEntityId

public data class TestUpdated(
    public val id: TestEntityId,
    public val title: String?,
    public val description: String?
)
