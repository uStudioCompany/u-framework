package com.dream.umbrella.lib.eventsourcing.store.event.query

import com.dream.umbrella.lib.eventsourcing.EventSourceRepositoryErrors
import com.dream.umbrella.lib.eventsourcing.aggregate.Revision
import com.dream.umbrella.lib.eventsourcing.event.EventName
import com.dream.umbrella.lib.eventsourcing.event.EventNameResolver
import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.fold
import io.github.airflux.functional.mapError
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getLong
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getString
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

internal object EventRowMapper {

    fun <NAME : EventName> mapping(
        row: Row,
        eventNameResolver: EventNameResolver<NAME>
    ): Result<EventRow<NAME>, EventSourceRepositoryErrors.Event> =
        Result {
            EventRow(
                correlationId = row.correlationId.bind(),
                commandId = row.commandId.bind(),
                name = row.name(eventNameResolver).bind(),
                revision = row.revision.bind(),
                data = row.data.bind()
            )
        }

    private val Row.commandId: Result<MessageId, EventSourceRepositoryErrors.Event>
        get() = getString(EventStoreMetadata.COMMAND_ID_COLUMN_NAME)
            .fold(
                onSuccess = {
                    MessageId.of(it!!) //TODO REFACTORING
                        .mapError { failure -> EventSourceRepositoryErrors.Event.InvalidData(failure) }
                },
                onError = { failure -> EventSourceRepositoryErrors.Event.Load(failure).error() }
            )

    private val Row.correlationId: Result<CorrelationId, EventSourceRepositoryErrors.Event>
        get() = getString(EventStoreMetadata.CORRELATION_ID_COLUMN_NAME)
            .fold(
                onSuccess = {
                    CorrelationId.of(it!!) //TODO REFACTORING
                        .mapError { failure -> EventSourceRepositoryErrors.Event.InvalidData(failure) }
                },
                onError = { failure -> EventSourceRepositoryErrors.Event.Load(failure).error() }
            )

    private fun <NAME : EventName> Row.name(
        eventNameResolver: EventNameResolver<NAME>
    ): Result<NAME, EventSourceRepositoryErrors.Event> =
        getString(EventStoreMetadata.EVENT_NAME_COLUMN_NAME)
            .fold(
                onSuccess = {
                    val name = it!! //TODO REFACTORING
                    eventNameResolver.resolve(name)
                        ?.success()
                        ?: EventSourceRepositoryErrors.Event.UnknownName(name).error()
                },
                onError = { failure -> EventSourceRepositoryErrors.Event.Load(failure).error() }
            )

    private val Row.revision: Result<Revision, EventSourceRepositoryErrors.Event>
        get() = getLong(EventStoreMetadata.EVENT_REVISION_COLUMN_NAME)
            .fold(
                onSuccess = {
                    Revision.of(it!!) //TODO REFACTORING
                        .mapError { failure -> EventSourceRepositoryErrors.Event.InvalidData(failure) }
                },
                onError = { failure -> EventSourceRepositoryErrors.Event.Load(failure).error() }
            )

    private val Row.data: Result<String, EventSourceRepositoryErrors.Event>
        get() = getString(EventStoreMetadata.EVENT_DATA_COLUMN_NAME)
            .fold(
                onSuccess = {
                    it!!.success() //TODO REFACTORING
                },
                onError = { failure -> EventSourceRepositoryErrors.Event.Load(failure).error() }
            )
}
