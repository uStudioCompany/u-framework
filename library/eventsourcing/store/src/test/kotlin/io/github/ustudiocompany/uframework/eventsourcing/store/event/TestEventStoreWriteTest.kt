package io.github.ustudiocompany.uframework.eventsourcing.store.event

import com.fasterxml.jackson.databind.ObjectMapper
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

internal class TestEventStoreWriteTest : AbstractEventSourcingStoreTest() {

    init {

        "when event store writer was initialized" - {
            val store = TestEventStore(container.dataSource, TABLE_NAME, ObjectMapper())

            "when trying to save" - {

                "a unique event" - {

                    "then the attempt should succeed" {
                        container.truncateTable(TABLE_NAME)
                        store.saveEvent(registeredEvent, retryScope()).shouldBeSuccess().value shouldBe true

                        container.checkData(SELECT_RECORD) {
                            getString(1) shouldBe AGGREGATE_ID
                            getString(2) shouldBe CORRELATION_ID
                            getString(3) shouldBe COMMAND_ID
                            getString(4) shouldBe registeredEvent.name.get
                            getLong(5) shouldBe Revision.initial.get

                            val data = """{"id":"$AGGREGATE_ID","title":"$TITLE","description":"$DESCRIPTION"}"""
                            getString(6) shouldBe data
                        }
                    }
                }

                "a non-unique event" - {

                    "then the attempt should failed" {
                        container.truncateTable(TABLE_NAME)
                        store.saveEvent(registeredEvent, retryScope()).shouldBeSuccess().value shouldBe true
                        store.saveEvent(registeredEvent, retryScope()).shouldBeSuccess().value shouldBe false
                    }
                }
            }
        }
    }

    companion object {
        private const val TABLE_NAME = "public.test_events"
        private const val AGGREGATE_ID = "ce4e5e7f-1abf-49fb-a520-eeb456bebe3b"
        private const val CORRELATION_ID = "2b0268bb-7bbe-46ba-abb5-2db4d37ecfec"
        private const val COMMAND_ID = "0dcf99ec-d940-4fd3-8f25-db2b73d5e670"
        private const val TITLE = "title-1"
        private const val DESCRIPTION = "description-1"

        private val aggregateId = TestEntityId(AGGREGATE_ID)
        private val correlationId = CorrelationId.of(CORRELATION_ID).getValue()
        private val commandId = MessageId.of(COMMAND_ID).getValue()

        private fun retryScope() = RetryScope.default()

        private val registeredEvent = TestEvent.Registered(
            commandId = commandId,
            correlationId = correlationId,
            revision = Revision.initial,
            data = TestRegistered(
                id = aggregateId,
                title = TITLE,
                description = DESCRIPTION
            )
        )

        private val SELECT_RECORD = """
            | SELECT aggregate_id,
            |        correlation_id,
            |        command_id,
            |        name,
            |        revision,
            |        data
            |   FROM $TABLE_NAME
            |  WHERE aggregate_id = '$AGGREGATE_ID'
            |    AND revision >= 0
            |  LIMIT 1
           """.trimMargin()
    }
}
