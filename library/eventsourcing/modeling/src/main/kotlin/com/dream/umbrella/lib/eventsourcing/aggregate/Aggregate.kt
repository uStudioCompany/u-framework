package com.dream.umbrella.lib.eventsourcing.aggregate

public abstract class Aggregate<ID : Any> {
    public abstract val id: ID
    public abstract val revisions: Revisions

    override fun equals(other: Any?): Boolean = this === other || (other is Aggregate<*> && this.id == other.id)
    override fun hashCode(): Int = id.hashCode()
}
