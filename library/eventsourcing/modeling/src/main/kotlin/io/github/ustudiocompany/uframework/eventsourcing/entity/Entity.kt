package io.github.ustudiocompany.uframework.eventsourcing.entity

public interface Entity<out ID>
    where ID : EntityId {

    public val id: ID
}
