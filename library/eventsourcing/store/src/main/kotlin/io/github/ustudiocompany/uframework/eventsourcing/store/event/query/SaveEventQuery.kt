package io.github.ustudiocompany.uframework.eventsourcing.store.event.query

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.fold
import io.github.ustudiocompany.uframework.eventsourcing.EventSourceRepositoryErrors
import io.github.ustudiocompany.uframework.eventsourcing.event.EventName
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.AGGREGATE_ID_COLUMN_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.AGGREGATE_ID_PARAM_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.COMMAND_ID_COLUMN_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.COMMAND_ID_PARAM_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.CORRELATION_ID_COLUMN_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.CORRELATION_ID_PARAM_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.EVENT_DATA_COLUMN_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.EVENT_DATA_PARAM_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.EVENT_NAME_COLUMN_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.EVENT_NAME_PARAM_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.EVENT_REVISION_COLUMN_NAME
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventStoreMetadata.EVENT_REVISION_PARAM_NAME
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.SqlInsert
import io.github.ustudiocompany.uframework.jdbc.sql.param.asSqlParam
import java.sql.Connection

internal class SaveEventQuery<NAME : EventName>(tableName: String) {

    private val query = SqlInsert(
        sql = ParametrizedSql.of(
            """
            | INSERT INTO $tableName (
            |    $AGGREGATE_ID_COLUMN_NAME,
            |    $CORRELATION_ID_COLUMN_NAME,
            |    $COMMAND_ID_COLUMN_NAME,
            |    $EVENT_NAME_COLUMN_NAME,
            |    $EVENT_REVISION_COLUMN_NAME,
            |    $EVENT_DATA_COLUMN_NAME
            | ) VALUES (
            |   :$AGGREGATE_ID_PARAM_NAME, 
            |   :$CORRELATION_ID_PARAM_NAME, 
            |   :$COMMAND_ID_PARAM_NAME, 
            |   :$EVENT_NAME_PARAM_NAME, 
            |   :$EVENT_REVISION_PARAM_NAME, 
            |   :$EVENT_DATA_PARAM_NAME
            | )
            """.trimMargin()
        )
    )

    operator fun invoke(
        connection: Connection,
        aggregateId: String,
        event: EventRow<NAME>
    ): Result<Boolean, EventSourceRepositoryErrors.Event> =
        query.execute(
            connection,
            aggregateId asSqlParam AGGREGATE_ID_PARAM_NAME,
            event.correlationId.get asSqlParam CORRELATION_ID_PARAM_NAME,
            event.commandId.get asSqlParam COMMAND_ID_PARAM_NAME,
            event.name.get asSqlParam EVENT_NAME_PARAM_NAME,
            event.revision.get asSqlParam EVENT_REVISION_PARAM_NAME,
            event.data asSqlParam EVENT_DATA_PARAM_NAME
        ).fold(
            onSuccess = { Result.of(it > 0) },
            onError = { failure ->
                when (failure) {
                    is JDBCErrors.Connection -> EventSourceRepositoryErrors.Event.Connection(failure).error()
                    is JDBCErrors.Data.DuplicateKeyValue -> Result.asFalse
                    else -> EventSourceRepositoryErrors.Event.Save(failure).error()
                }
            }
        )
}
