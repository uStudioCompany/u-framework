package io.github.ustudiocompany.uframework.eventsourcing.store.snapshot

import io.github.airflux.functional.kotest.getValue
import io.github.airflux.functional.kotest.shouldBeSuccess
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.History
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revision
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revisions
import io.github.ustudiocompany.uframework.eventsourcing.store.AbstractEventSourcingStoreTest
import io.github.ustudiocompany.uframework.eventsourcing.store.model.TestAggregate
import io.github.ustudiocompany.uframework.eventsourcing.store.model.TestEntity
import io.github.ustudiocompany.uframework.eventsourcing.store.model.TestEntityId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.retry.RetryScope
import io.kotest.matchers.shouldBe

internal class TestSnapshotStoreWriteTest : AbstractEventSourcingStoreTest() {

    init {

        "when snapshot store was initialized" - {
            val store = TestSnapshotStore(container.dataSource, TABLE_NAME, mapper, TestHistorySerializer(mapper))

            "when trying to save first snapshot" - {

                "then the attempt should succeed" {
                    container.truncateTable(TABLE_NAME)
                    store.saveSnapshot(registeredAggregate, retryScope()).shouldBeSuccess().value shouldBe true

                    container.checkData(SELECT_RECORD) {
                        getString(1) shouldBe AGGREGATE_ID
                        getLong(2) shouldBe Revision.initial.get
                        getString(3) shouldBe """[{"revision":0,"commandId":"$REGISTRE_COMMAND_ID"}]"""
                        getString(4) shouldBe """{"id":"$AGGREGATE_ID","title":"$TITLE","description":"$DESCRIPTION"}"""
                    }
                }
            }

            "when trying to save new revision of the snapshot" - {

                "then the attempt should succeed" {
                    container.truncateTable(TABLE_NAME)
                    store.saveSnapshot(registeredAggregate, retryScope()).shouldBeSuccess().value shouldBe true
                    store.saveSnapshot(updatedAggregate, retryScope()).shouldBeSuccess().value shouldBe true

                    container.checkData(SELECT_RECORD) {
                        getString(1) shouldBe AGGREGATE_ID
                        getLong(2) shouldBe Revision.initial.next().get
                        getString(3) shouldBe """[{"revision":0,"commandId":"$REGISTRE_COMMAND_ID"},{"revision":1,"commandId":"$UPDATE_COMMAND_ID"}]"""
                        getString(4) shouldBe """{"id":"$AGGREGATE_ID","title":"$TITLE","description":"$NEW_DESCRIPTION"}"""
                    }
                }
            }
        }
    }

    companion object {
        private const val TABLE_NAME = "public.test_snapshots"
        private const val AGGREGATE_ID = "ce4e5e7f-1abf-49fb-a520-eeb456bebe3b"
        private const val REGISTRE_COMMAND_ID = "0dcf99ec-d940-4fd3-8f25-db2b73d5e670"
        private const val UPDATE_COMMAND_ID = "dbaef9ac-3e47-434e-80a8-81fa60bceb7b"
        private const val TITLE = "title-1"
        private const val DESCRIPTION = "description-1"
        private const val NEW_DESCRIPTION = "description-2"

        private val aggregateId = TestEntityId(AGGREGATE_ID)
        private val registreCommandId = MessageId.of(REGISTRE_COMMAND_ID).getValue()
        private val updateCommandId = MessageId.of(UPDATE_COMMAND_ID).getValue()

        private fun retryScope() = RetryScope.default()

        private val registeredAggregate = TestAggregate(
            revisions = Revisions.of(
                current = Revision.initial,
                history = History.of(
                    listOf(History.Point(revision = Revision.initial, commandId = registreCommandId))
                )
            ),
            entity = TestEntity(
                id = aggregateId,
                title = TITLE,
                description = DESCRIPTION
            )
        )

        private val updatedAggregate = run {
            val revisions = registeredAggregate.revisions
                .let { revisions ->
                    val revision = revisions.current.next()
                    val history = History.of(revisions.history.toList() + History.Point(revision, updateCommandId))
                    Revisions.of(
                        current = revision,
                        history = history
                    )
                }

            registeredAggregate.copy(
                revisions = revisions,
                entity = registeredAggregate.entity.copy(description = NEW_DESCRIPTION)
            )
        }

        private val SELECT_RECORD = """
            | SELECT aggregate_id,
            |        revision,
            |        history,
            |        data
            |   FROM $TABLE_NAME
            |  WHERE aggregate_id = '$AGGREGATE_ID'
           """.trimMargin()
    }
}
