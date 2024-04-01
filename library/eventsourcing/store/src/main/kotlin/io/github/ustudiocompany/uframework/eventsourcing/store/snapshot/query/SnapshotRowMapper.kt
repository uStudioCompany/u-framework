package io.github.ustudiocompany.uframework.eventsourcing.store.snapshot.query

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.fold
import io.github.airflux.functional.mapError
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.eventsourcing.EventSourceRepositoryErrors
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.History
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revision
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revisions
import io.github.ustudiocompany.uframework.eventsourcing.store.snapshot.HistorySerializer
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getLong
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getString

internal object SnapshotRowMapper {

    fun mapping(
        row: Row,
        serializer: HistorySerializer<String>
    ): Result<SnapshotRow, EventSourceRepositoryErrors.Snapshot> =
        Result {
            val (revision) = row.revision
            val (history) = row.history(serializer)
            val (data) = row.data
            SnapshotRow(
                revisions = Revisions.of(revision, history),
                data = data
            )
        }

    private fun Row.history(
        serializer: HistorySerializer<String>
    ): Result<History, EventSourceRepositoryErrors.Snapshot> =
        getString(SnapshotStoreMetadata.SNAPSHOT_HISTORY_COLUMN_NAME)
            .fold(
                onSuccess = {
                    it!!.deserializationHistory(serializer)
                },
                onError = { failure -> EventSourceRepositoryErrors.Snapshot.Load(failure).error() }
            )

    private val Row.revision: Result<Revision, EventSourceRepositoryErrors.Snapshot>
        get() = getLong(SnapshotStoreMetadata.SNAPSHOT_REVISION_COLUMN_NAME)
            .fold(
                onSuccess = {
                    Revision.of(it!!) //TODO REFACTORING
                        .mapError { failure -> EventSourceRepositoryErrors.Snapshot.InvalidData(failure) }
                },
                onError = { failure -> EventSourceRepositoryErrors.Snapshot.Load(failure).error() }
            )

    private val Row.data: Result<String, EventSourceRepositoryErrors.Snapshot.Load>
        get() = getString(SnapshotStoreMetadata.SNAPSHOT_DATA_COLUMN_NAME)
            .fold(
                onSuccess = {
                    it!!.success() //TODO REFACTORING
                },
                onError = { failure -> EventSourceRepositoryErrors.Snapshot.Load(failure).error() }
            )

    private fun String.deserializationHistory(
        serializer: HistorySerializer<String>
    ): Result<History, EventSourceRepositoryErrors.Snapshot.History.Deserialization> =
        serializer.deserialize(this)
            .mapError { failure -> EventSourceRepositoryErrors.Snapshot.History.Deserialization(failure) }
}
