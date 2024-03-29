package com.dream.umbrella.lib.eventsourcing.store.snapshot.query

import com.dream.umbrella.lib.eventsourcing.EventSourceRepositoryErrors
import com.dream.umbrella.lib.eventsourcing.store.snapshot.HistorySerializer
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.AGGREGATE_ID_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.AGGREGATE_ID_PARAM_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.SNAPSHOT_DATA_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.SNAPSHOT_HISTORY_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotStoreMetadata.SNAPSHOT_REVISION_COLUMN_NAME
import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.param.asSqlParam
import io.github.ustudiocompany.uframework.jdbc.sql.rowMappingQuery
import java.sql.Connection

internal class LoadSnapshotQuery(
    tableName: String,
    historySerializer: HistorySerializer<String>
) {

    private val query = rowMappingQuery(
        sql = ParametrizedSql.of(
            """
            | SELECT $SNAPSHOT_REVISION_COLUMN_NAME,
            |        $SNAPSHOT_HISTORY_COLUMN_NAME,
            |        $SNAPSHOT_DATA_COLUMN_NAME
            |   FROM $tableName
            |  WHERE $AGGREGATE_ID_COLUMN_NAME = :$AGGREGATE_ID_PARAM_NAME 
            """.trimMargin()
        ),
        { failure ->
            if (failure is JDBCErrors.Connection)
                EventSourceRepositoryErrors.Snapshot.Connection(failure)
            else
                EventSourceRepositoryErrors.Snapshot.Load(failure)
        },
        { row -> SnapshotRowMapper.mapping(row, historySerializer) }
    )

    operator fun invoke(
        connection: Connection,
        id: String
    ): Result<SnapshotRow?, EventSourceRepositoryErrors.Snapshot> =
        query.execute(connection, id asSqlParam AGGREGATE_ID_PARAM_NAME)
}
