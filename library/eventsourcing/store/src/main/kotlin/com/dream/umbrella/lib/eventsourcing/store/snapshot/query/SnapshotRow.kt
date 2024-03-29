package com.dream.umbrella.lib.eventsourcing.store.snapshot.query

import com.dream.umbrella.lib.eventsourcing.aggregate.Revisions

public class SnapshotRow(
    public val revisions: Revisions,
    public val data: String
)
