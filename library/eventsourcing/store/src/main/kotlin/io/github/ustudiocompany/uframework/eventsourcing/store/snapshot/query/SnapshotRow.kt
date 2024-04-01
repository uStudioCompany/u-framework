package io.github.ustudiocompany.uframework.eventsourcing.store.snapshot.query

import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revisions

public class SnapshotRow(
    public val revisions: Revisions,
    public val data: String
)
