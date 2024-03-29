package com.dream.umbrella.lib.eventsourcing.store.snapshot

import com.dream.umbrella.lib.eventsourcing.EventSourceRepositoryErrors
import com.dream.umbrella.lib.eventsourcing.aggregate.Aggregate
import com.dream.umbrella.lib.eventsourcing.aggregate.Revisions
import com.dream.umbrella.lib.eventsourcing.entity.EntityId
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.LoadSnapshotQuery
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SaveSnapshotQuery
import com.dream.umbrella.lib.eventsourcing.store.snapshot.query.SnapshotRow
import io.github.airflux.functional.Result
import io.github.airflux.functional.flatMap
import io.github.airflux.functional.getOrForward
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.useConnection
import io.github.ustudiocompany.uframework.retry.RetryScope
import io.github.ustudiocompany.uframework.retry.retry
import javax.sql.DataSource

public abstract class SnapshotStore<AGGREGATE, ID>(
    private val dataSource: DataSource,
    historySerializer: HistorySerializer<String>,
    tableName: String
) where AGGREGATE : Aggregate<ID>,
        ID : EntityId {

    protected abstract fun createSnapshot(
        row: SnapshotRow
    ): Result<AGGREGATE, EventSourceRepositoryErrors.Snapshot.Deserialization>

    protected abstract fun serializeData(
        aggregate: AGGREGATE
    ): Result<String, EventSourceRepositoryErrors.Snapshot.Serialization>

    public fun loadSnapshot(
        id: ID,
        retryScope: RetryScope
    ): Result<AGGREGATE?, EventSourceRepositoryErrors.Snapshot> =
        retry(retryScope, retryPredicate) {
            dataSource.useConnection(errorConvertor) { connection ->
                loadSnapshotQuery(connection, id.asString())
            }
        }.flatMap { row ->
            if (row != null)
                createSnapshot(row)
            else
                Result.asNull
        }

    public fun saveSnapshot(
        aggregate: AGGREGATE,
        retryScope: RetryScope
    ): Result<Boolean, EventSourceRepositoryErrors.Snapshot> {
        val row = aggregate.toSnapshotRow().getOrForward { return it }
        return retry(retryScope, retryPredicate) {
            dataSource.useConnection(errorConvertor) { connection ->
                saveSnapshotQuery(connection, aggregate.id.asString(), row)
            }
        }
    }

    private fun AGGREGATE.toSnapshotRow(): Result<SnapshotRow, EventSourceRepositoryErrors.Snapshot> {
        val data = serializeData(this).getOrForward { return it }
        return SnapshotRow(
            revisions = Revisions.of(revisions.current, revisions.history),
            data = data
        ).success()
    }

    private val errorConvertor: ErrorConverter<EventSourceRepositoryErrors.Snapshot> = { failure ->
        EventSourceRepositoryErrors.Snapshot.Load(failure)
    }

    private val retryPredicate: (EventSourceRepositoryErrors) -> Boolean = { failure ->
        failure is EventSourceRepositoryErrors.Snapshot.Connection
    }

    private val loadSnapshotQuery = LoadSnapshotQuery(tableName, historySerializer)
    private val saveSnapshotQuery = SaveSnapshotQuery(tableName, historySerializer)
}
