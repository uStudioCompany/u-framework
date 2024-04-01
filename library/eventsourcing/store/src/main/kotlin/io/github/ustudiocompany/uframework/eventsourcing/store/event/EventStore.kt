package io.github.ustudiocompany.uframework.eventsourcing.store.event

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.flatMap
import io.github.airflux.functional.getOrForward
import io.github.airflux.functional.success
import io.github.airflux.functional.traverse
import io.github.ustudiocompany.uframework.eventsourcing.EventSourceRepositoryErrors
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revision
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.eventsourcing.event.EventName
import io.github.ustudiocompany.uframework.eventsourcing.event.EventNameResolver
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventRow
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.LoadAllEventsQuery
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.LoadEventQuery
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.SaveEventQuery
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.useConnection
import io.github.ustudiocompany.uframework.retry.RetryScope
import io.github.ustudiocompany.uframework.retry.retry
import javax.sql.DataSource

@Suppress("TooManyFunctions")
public abstract class EventStore<EVENT, ID, NAME>(
    private val dataSource: DataSource,
    eventNameResolver: EventNameResolver<NAME>,
    tableName: String
) where EVENT : Event<ID, NAME>,
        ID : EntityId,
        NAME : EventName {

    protected abstract fun createEvent(
        row: EventRow<NAME>
    ): Result<EVENT, EventSourceRepositoryErrors.Event.Deserialization>

    protected abstract fun serializeData(
        event: EVENT
    ): Result<String, EventSourceRepositoryErrors.Event.Serialization>

    public fun loadEvents(
        id: ID,
        revision: Revision,
        maxCount: Int,
        retryScope: RetryScope
    ): Result<List<EVENT>, EventSourceRepositoryErrors.Event> =
        retry(retryScope, retryPredicate) {
            dataSource.useConnection(errorConvertor) { connection ->
                loadAllEventsQuery.invoke(connection, id.asString(), revision, maxCount)
            }
        }.flatMap { rows ->
            rows.traverse { row -> createEvent(row) }
        }

    public fun loadEvent(
        id: ID,
        revision: Revision,
        retryScope: RetryScope
    ): Result<EVENT, EventSourceRepositoryErrors.Event> =
        retry(retryScope, retryPredicate) {
            dataSource.useConnection(errorConvertor) { connection ->
                loadEventQuery(connection, id.asString(), revision)
            }
        }.flatMap { row ->
            if (row != null)
                createEvent(row)
            else
                EventSourceRepositoryErrors.Event.Missing.error()
        }

    public fun saveEvent(
        event: EVENT,
        retryScope: RetryScope
    ): Result<Boolean, EventSourceRepositoryErrors.Event> {
        val aggregateId = event.aggregateId.asString()
        val row = event.toEventRow().getOrForward { return it }
        return retry(retryScope, retryPredicate) {
            dataSource.useConnection(errorConvertor) { connection ->
                saveEventQuery(connection, aggregateId, row)
            }
        }
    }

    private val loadAllEventsQuery = LoadAllEventsQuery(tableName, eventNameResolver)
    private val loadEventQuery = LoadEventQuery(tableName, eventNameResolver)
    private val saveEventQuery = SaveEventQuery<NAME>(tableName)

    private val errorConvertor: ErrorConverter<EventSourceRepositoryErrors.Event> = { failure ->
        EventSourceRepositoryErrors.Event.Load(failure)
    }

    private val retryPredicate: (EventSourceRepositoryErrors) -> Boolean = { failure ->
        failure is EventSourceRepositoryErrors.Event.Connection
    }

    private fun EVENT.toEventRow(): Result<EventRow<NAME>, EventSourceRepositoryErrors.Event.Serialization> {
        val result = EventRow(
            correlationId = correlationId,
            commandId = commandId,
            name = name,
            revision = revision,
            data = serializeData(this).getOrForward { return it }
        )
        return result.success()
    }
}
