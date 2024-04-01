package io.github.ustudiocompany.uframework.eventsourcing.store.event

import io.github.ustudiocompany.uframework.eventsourcing.event.EventNameResolver
import io.github.ustudiocompany.uframework.eventsourcing.store.event.model.TestEvent

internal object TestEventNameResolver : EventNameResolver<TestEvent.Name> {
    private val elements = TestEvent.Name.entries.associateBy { it.get.uppercase() }
    override fun resolve(value: String): TestEvent.Name? = elements[value.uppercase()]
}
