package com.dream.umbrella.lib.eventsourcing.store.event.query

internal object EventStoreMetadata {

    const val AGGREGATE_ID_COLUMN_NAME: String = "aggregate_id"
    const val COMMAND_ID_COLUMN_NAME: String = "command_id"
    const val CORRELATION_ID_COLUMN_NAME: String = "correlation_id"
    const val EVENT_NAME_COLUMN_NAME: String = "name"
    const val EVENT_REVISION_COLUMN_NAME: String = "revision"
    const val EVENT_DATA_COLUMN_NAME: String = "data"

    const val AGGREGATE_ID_PARAM_NAME: String = "aggregate_id"
    const val CORRELATION_ID_PARAM_NAME: String = "correlation_id"
    const val COMMAND_ID_PARAM_NAME: String = "command_id"
    const val EVENT_NAME_PARAM_NAME: String = "name"
    const val EVENT_REVISION_PARAM_NAME: String = "revision"
    const val EVENT_DATA_PARAM_NAME: String = "data"
    const val LIMIT_PARAM_NAME: String = "limit"
}
