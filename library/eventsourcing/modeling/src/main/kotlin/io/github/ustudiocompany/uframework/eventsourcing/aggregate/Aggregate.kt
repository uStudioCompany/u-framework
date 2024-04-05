package io.github.ustudiocompany.uframework.eventsourcing.aggregate

public interface Aggregate<out ID : Any> {
    public val id: ID
    public val history: History
}
