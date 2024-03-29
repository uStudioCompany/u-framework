package com.dream.umbrella.lib.eventsourcing.event

public fun interface EventNameResolver<NAME>
    where NAME : EventName {

    public fun resolve(value: String): NAME?
}
