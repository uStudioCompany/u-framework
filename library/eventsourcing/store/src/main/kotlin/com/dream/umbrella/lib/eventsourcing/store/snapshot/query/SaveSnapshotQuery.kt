package com.dream.umbrella.lib.eventsourcing.store.snapshot.query

import com.dream.umbrella.lib.eventsourcing.EventSourceRepositoryErrors
import com.dream.umbrella.lib.eventsourcing.aggregate.History
import com.dream.umbrella.lib.eventsourcing.store.snapshot.HistorySerializer
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.AGGREGATE_ID_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.AGGREGATE_ID_PARAM_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.SNAPSHOT_DATA_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.SNAPSHOT_DATA_PARAM_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.SNAPSHOT_HISTORY_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.SNAPSHOT_HISTORY_PARAM_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.SNAPSHOT_REVISION_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.SNAPSHOT_REVISION_PARAM_NAME
import io.github.airflux.functional.Result
import io.github.airflux.functional.ResultWith
import io.github.airflux.functional.error
import io.github.airflux.functional.fold
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.SqlInsert
import io.github.ustudiocompany.uframework.jdbc.sql.param.asSqlParam
import java.sql.Connection

internal class SaveSnapshotQuery(
    tableName: String,
    private val historySerializer: HistorySerializer<String>
) {

    private val query = SqlInsert(
        sql = ParametrizedSql.of(
            """
            | INSERT INTO $tableName (
            |    $AGGREGATE_ID_COLUMN_NAME,
            |    $SNAPSHOT_REVISION_COLUMN_NAME,
            |    $SNAPSHOT_HISTORY_COLUMN_NAME,
            |    $SNAPSHOT_DATA_COLUMN_NAME
            | ) VALUES (
            |   :$AGGREGATE_ID_PARAM_NAME, 
            |   :$SNAPSHOT_REVISION_PARAM_NAME, 
            |   :$SNAPSHOT_HISTORY_PARAM_NAME, 
            |   :$SNAPSHOT_DATA_PARAM_NAME
            | ) ON CONFLICT($AGGREGATE_ID_COLUMN_NAME) 
            |   DO UPDATE SET $SNAPSHOT_REVISION_COLUMN_NAME = EXCLUDED.$SNAPSHOT_REVISION_COLUMN_NAME,
            |                 $SNAPSHOT_HISTORY_COLUMN_NAME = EXCLUDED.$SNAPSHOT_HISTORY_COLUMN_NAME,
            |                 $SNAPSHOT_DATA_COLUMN_NAME = EXCLUDED.$SNAPSHOT_DATA_COLUMN_NAME
            """.trimMargin()
        )
    )

    operator fun invoke(
        connection: Connection,
        aggregateId: String,
        snapshot: SnapshotRow
    ): Result<Boolean, EventSourceRepositoryErrors.Snapshot> =
        ResultWith {
            val (history) = serializationHistory(snapshot.revisions.history)

            query.execute(
                connection,
                aggregateId asSqlParam AGGREGATE_ID_PARAM_NAME,
                snapshot.revisions.current.get asSqlParam SNAPSHOT_REVISION_PARAM_NAME,
                history asSqlParam SNAPSHOT_HISTORY_PARAM_NAME,
                snapshot.data asSqlParam SNAPSHOT_DATA_PARAM_NAME
            ).fold(
                onSuccess = { Result.of(it > 0) },
                onError = { failure ->
                    when (failure) {
                        is JDBCErrors.Connection -> EventSourceRepositoryErrors.Snapshot.Connection(failure).error()
                        is JDBCErrors.Data.DuplicateKeyValue -> Result.asFalse
                        else -> EventSourceRepositoryErrors.Snapshot.Save(failure).error()
                    }
                }
            )
        }

    private fun serializationHistory(
        history: History
    ): Result<String, EventSourceRepositoryErrors.Snapshot.History.Serialization> =
        historySerializer.serialize(history).success()
}
