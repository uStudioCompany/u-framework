package com.dream.umbrella.lib.eventsourcing.store.snapshot.query

internal object SnapshotStoreMetadata {

    const val AGGREGATE_ID_COLUMN_NAME: String = "aggregate_id"
    const val SNAPSHOT_REVISION_COLUMN_NAME: String = "revision"
    const val SNAPSHOT_HISTORY_COLUMN_NAME: String = "history"
    const val SNAPSHOT_DATA_COLUMN_NAME: String = "data"

    const val AGGREGATE_ID_PARAM_NAME: String = "aggregate_id"
    const val SNAPSHOT_REVISION_PARAM_NAME: String = "revision"
    const val SNAPSHOT_HISTORY_PARAM_NAME: String = "history"
    const val SNAPSHOT_DATA_PARAM_NAME: String = "data"
}
