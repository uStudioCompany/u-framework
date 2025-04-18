package io.github.ustudiocompany.uframework.eventsourcing.event

import io.github.ustudiocompany.uframework.eventsourcing.model.TestEntityId

internal data class TestRegistered(
    val id: TestEntityId,
    val title: String?,
    val description: String?
)
