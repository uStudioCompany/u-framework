package io.github.ustudiocompany.uframework.eventsourcing.event

public fun interface EventNameResolver<NAME>
    where NAME : EventName {

    public fun resolve(value: String): NAME?
}
