package io.github.ustudiocompany.uframework.eventsourcing.store.event

import io.github.airflux.functional.kotest.getValue
import io.github.airflux.functional.kotest.shouldBeSuccess
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revision
import io.github.ustudiocompany.uframework.eventsourcing.store.AbstractEventSourcingStoreTest
import io.github.ustudiocompany.uframework.eventsourcing.store.event.model.TestEvent
import io.github.ustudiocompany.uframework.eventsourcing.store.event.model.TestRegistered
import io.github.ustudiocompany.uframework.eventsourcing.store.model.TestEntityId
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.retry.RetryScope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class TestEventStoreReadTest : AbstractEventSourcingStoreTest() {

    init {

        "when event store reader was initialized" - {
            val store = TestEventStore(container.dataSource, TABLE_NAME, mapper)

            "when trying to read" - {

                "a existent event" - {
                    container.truncateTable(TABLE_NAME)
                    val data = """{"id":"$AGGREGATE_ID","title":"$TITLE","description":"$DESCRIPTION"}"""
                    container.executeSql(insertEventSQL(data))

                    "then should return an non-empty stream" {
                        val result = store.loadEvents(
                            id = event.aggregateId,
                            revision = event.revision,
                            maxCount = 1,
                            retryScope = retryScope()
                        )

                        val stream = result.shouldBeSuccess().value
                        stream.isEmpty() shouldBe false
                        stream.forEach { actualEvent ->
                            actualEvent.shouldBeInstanceOf<TestEvent.Registered>()

                            actualEvent.aggregateId shouldBe aggregateId
                            actualEvent.commandId shouldBe commandId
                            actualEvent.correlationId shouldBe correlationId
                            actualEvent.revision shouldBe Revision.initial

                            val expected = event.data
                            val actual = actualEvent.data
                            expected shouldBe actual
                        }
                    }
                }

                "a non-existent event" - {
                    container.truncateTable(TABLE_NAME)

                    "then should return an empty stream" {
                        val result = store.loadEvents(
                            id = event.aggregateId,
                            revision = event.revision,
                            maxCount = 1,
                            retryScope = retryScope()
                        )

                        val stream = result.shouldBeSuccess().value
                        stream.isEmpty() shouldBe true
                    }
                }
            }
        }
    }

    companion object {
        private const val TABLE_NAME = "public.test_events"

        private const val AGGREGATE_ID = "ce4e5e7f-1abf-49fb-a520-eeb456bebe3b"
        private const val TITLE = "title-1"
        private const val DESCRIPTION = "description-1"

        private val aggregateId = TestEntityId(AGGREGATE_ID)
        private val correlationId = CorrelationId.of("2b0268bb-7bbe-46ba-abb5-2db4d37ecfec").getValue()
        private val commandId = MessageId.of("0dcf99ec-d940-4fd3-8f25-db2b73d5e670").getValue()

        private fun retryScope() = RetryScope.default()

        private val event = TestEvent.Registered(
            commandId = commandId,
            correlationId = correlationId,
            revision = Revision.initial,
            data = TestRegistered(
                id = aggregateId,
                title = TITLE,
                description = DESCRIPTION
            )
        )

        private fun insertEventSQL(data: String) = """
            | INSERT INTO  $TABLE_NAME (
            |   aggregate_id,
            |   command_id,
            |   correlation_id,
            |   name,
            |   revision,
            |   data
            | ) VALUES (
            |   '${event.aggregateId.get}',
            |   '${event.commandId.get}',
            |   '${event.correlationId.get}',
            |   '${event.name.get}',
            |   ${event.revision.get},
            |   '$data'
            | )
           """.trimMargin()
    }
}
