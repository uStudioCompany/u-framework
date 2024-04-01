package io.github.ustudiocompany.uframework.eventsourcing.store.event

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.eventsourcing.EventSourceRepositoryErrors
import io.github.ustudiocompany.uframework.eventsourcing.store.event.model.TestEvent
import io.github.ustudiocompany.uframework.eventsourcing.store.event.model.TestRegistered
import io.github.ustudiocompany.uframework.eventsourcing.store.event.model.TestUpdated
import io.github.ustudiocompany.uframework.eventsourcing.store.event.query.EventRow
import io.github.ustudiocompany.uframework.eventsourcing.store.model.TestEntityId
import javax.sql.DataSource

internal class TestEventStore(
    dataSource: DataSource,
    tableName: String,
    private val mapper: ObjectMapper
) : EventStore<TestEvent, TestEntityId, TestEvent.Name>(dataSource, TestEventNameResolver, tableName) {

    override fun createEvent(
        row: EventRow<TestEvent.Name>
    ): Result<TestEvent, EventSourceRepositoryErrors.Event.Deserialization> {
        val event = when (row.name) {
            TestEvent.Name.REGISTERED ->
                TestEvent.Registered(
                    commandId = row.commandId,
                    correlationId = row.correlationId,
                    revision = row.revision,
                    data = mapper.readValue(row.data, TestRegisteredData::class.java)
                        .let { data ->
                            TestRegistered(
                                id = TestEntityId(data.id),
                                title = data.title,
                                description = data.description
                            )
                        }
                )

            TestEvent.Name.UPDATED ->
                TestEvent.Updated(
                    commandId = row.commandId,
                    correlationId = row.correlationId,
                    revision = row.revision,
                    data = mapper.readValue(row.data, TestUpdatedData::class.java)
                        .let { data ->
                            TestUpdated(
                                id = TestEntityId(data.id),
                                title = data.title,
                                description = data.description
                            )
                        }
                )
        }

        return event.success()
    }

    override fun serializeData(event: TestEvent): Result<String, EventSourceRepositoryErrors.Event.Serialization> =
        try {
            val serializedEventData = when (event) {
                is TestEvent.Registered -> event.data
                    .let {
                        val data = TestRegisteredData(
                            id = it.id.asString(),
                            title = it.title,
                            description = it.description
                        )
                        mapper.writeValueAsString(data)
                    }

                is TestEvent.Updated -> event.data
                    .let {
                        val data = TestUpdatedData(
                            id = it.id.asString(),
                            title = it.title,
                            description = it.description
                        )
                        mapper.writeValueAsString(data)
                    }
            }
            serializedEventData.success()
        } catch (expected: Exception) {
            EventSourceRepositoryErrors.Event.Serialization(expected).error()
        }

    private class TestRegisteredData(
        @JsonProperty("id") val id: String,
        @JsonProperty("title") val title: String?,
        @JsonProperty("description") val description: String?
    )

    private class TestUpdatedData(
        @JsonProperty("id") val id: String,
        @JsonProperty("title") val title: String?,
        @JsonProperty("description") val description: String?
    )
}
