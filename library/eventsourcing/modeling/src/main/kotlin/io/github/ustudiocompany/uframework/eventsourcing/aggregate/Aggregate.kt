package io.github.ustudiocompany.uframework.eventsourcing.aggregate

public interface Aggregate<ID : Any> {
    public val id: ID
    public val revisions: Revisions
}
