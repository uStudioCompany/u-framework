package com.dream.umbrella.lib.eventsourcing.entity

public interface Entity<out ID : Any> {
    public val id: ID
}
