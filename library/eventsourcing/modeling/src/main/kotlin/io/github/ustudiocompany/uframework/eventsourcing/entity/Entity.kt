package io.github.ustudiocompany.uframework.eventsourcing.entity

public interface Entity<out ID : Any> {
    public val id: ID
}
