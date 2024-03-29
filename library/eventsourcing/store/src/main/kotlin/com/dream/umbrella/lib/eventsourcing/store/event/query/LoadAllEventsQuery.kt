package com.dream.umbrella.lib.eventsourcing.store.event.query

import com.dream.umbrella.lib.eventsourcing.EventSourceRepositoryErrors
import com.dream.umbrella.lib.eventsourcing.aggregate.Revision
import com.dream.umbrella.lib.eventsourcing.event.EventName
import com.dream.umbrella.lib.eventsourcing.event.EventNameResolver
import com.dream.umbrella.lib.eventsourcing.store.event.query.EventStoreMetadata.AGGREGATE_ID_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.event.query.EventStoreMetadata.AGGREGATE_ID_PARAM_NAME
import com.dream.umbrella.lib.eventsourcing.store.event.query.EventStoreMetadata.COMMAND_ID_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.event.query.EventStoreMetadata.CORRELATION_ID_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.event.query.EventStoreMetadata.EVENT_DATA_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.event.query.EventStoreMetadata.EVENT_NAME_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.event.query.EventStoreMetadata.EVENT_REVISION_COLUMN_NAME
import com.dream.umbrella.lib.eventsourcing.store.event.query.EventStoreMetadata.EVENT_REVISION_PARAM_NAME
import com.dream.umbrella.lib.eventsourcing.store.event.query.EventStoreMetadata.LIMIT_PARAM_NAME
import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.param.asSqlParam
import io.github.ustudiocompany.uframework.jdbc.sql.rowsMappingQuery
import java.sql.Connection

internal class LoadAllEventsQuery<NAME : EventName>(
    tableName: String,
    private val eventNameResolver: EventNameResolver<NAME>
) {

    private val query = rowsMappingQuery(
        sql = ParametrizedSql.of(
            """
            | SELECT $AGGREGATE_ID_COLUMN_NAME,
            |        $COMMAND_ID_COLUMN_NAME,
            |        $CORRELATION_ID_COLUMN_NAME,
            |        $EVENT_NAME_COLUMN_NAME,
            |        $EVENT_REVISION_COLUMN_NAME,
            |        $EVENT_DATA_COLUMN_NAME
            |   FROM $tableName
            |  WHERE $AGGREGATE_ID_COLUMN_NAME = :$AGGREGATE_ID_PARAM_NAME
            |    AND $EVENT_REVISION_COLUMN_NAME >= :$EVENT_REVISION_PARAM_NAME
            |  LIMIT :$LIMIT_PARAM_NAME
            """.trimMargin()
        ),
        { failure ->
            if (failure is JDBCErrors.Connection)
                EventSourceRepositoryErrors.Event.Connection(failure)
            else
                EventSourceRepositoryErrors.Event.Load(failure)
        },
        { row -> EventRowMapper.mapping(row, eventNameResolver) }
    )

    operator fun invoke(
        connection: Connection,
        id: String,
        revision: Revision,
        maxCount: Int
    ): Result<List<EventRow<NAME>>, EventSourceRepositoryErrors.Event> =
        query.execute(
            connection,
            id asSqlParam AGGREGATE_ID_PARAM_NAME,
            revision.get asSqlParam EVENT_REVISION_PARAM_NAME,
            maxCount asSqlParam LIMIT_PARAM_NAME
        )
}
