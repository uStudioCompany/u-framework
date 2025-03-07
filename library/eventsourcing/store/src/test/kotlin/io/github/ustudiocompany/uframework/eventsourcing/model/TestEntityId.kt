package io.github.ustudiocompany.uframework.eventsourcing.model

import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId

internal class TestEntityId(public val get: String) : EntityId {
    override fun asString(): String = get
}
