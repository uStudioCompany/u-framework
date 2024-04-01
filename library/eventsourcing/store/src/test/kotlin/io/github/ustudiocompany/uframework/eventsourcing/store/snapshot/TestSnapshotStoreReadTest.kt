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
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

internal class TestSnapshotStoreReadTest : AbstractEventSourcingStoreTest() {

    init {

        "when snapshot store was initialized" - {
            val store = TestSnapshotStore(container.dataSource, TABLE_NAME, mapper, TestHistorySerializer(mapper))

            "when trying to read an existing last snapshot" - {
                container.truncateTable(TABLE_NAME)
                container.executeSql(insertSnapshotSQL(HISTORY, DATA))

                "then should return the snapshot" {
                    val result = store.loadSnapshot(aggregateId, retryScope())

                    val snapshot = result.shouldBeSuccess().value
                    snapshot.shouldNotBeNull()
                    snapshot.id shouldBe aggregateId
                    snapshot.revisions.current.get shouldBe aggregate.revisions.current.get
                    snapshot.revisions.history shouldBe aggregate.revisions.history
                    snapshot.entity.id shouldBe aggregateId
                    snapshot.entity.title shouldBe TITLE
                    snapshot.entity.description shouldBe DESCRIPTION
                }
            }

            "when trying to read a non-existent snapshot" - {
                container.truncateTable(TABLE_NAME)

                "then should return an empty result" {
                    val result = store.loadSnapshot(aggregateId, retryScope())

                    val snapshot = result.shouldBeSuccess().value
                    snapshot.shouldBeNull()
                }
            }
        }
    }

    companion object {
        private const val TABLE_NAME = "public.test_snapshots"
        private const val AGGREGATE_ID = "ce4e5e7f-1abf-49fb-a520-eeb456bebe3b"
        private const val TITLE = "title-1"
        private const val DESCRIPTION = "description-1"

        private val aggregateId = TestEntityId(AGGREGATE_ID)
        private val registreCommandId = MessageId.of("0dcf99ec-d940-4fd3-8f25-db2b73d5e670").getValue()

        private val HISTORY = """[{"revision":0,"commandId":"${registreCommandId.get}"}]"""
        private const val DATA = """{"id":"$AGGREGATE_ID","title":"title-1","description":"description-1"}"""

        private fun retryScope() = RetryScope.default()

        private val aggregate = TestAggregate(
            revisions = Revisions.of(
                current = Revision.initial,
                history = History.of(
                    listOf(
                        History.Point(
                            revision = Revision.initial,
                            commandId = registreCommandId
                        )
                    )
                )
            ),
            entity = TestEntity(
                id = aggregateId,
                title = TITLE,
                description = DESCRIPTION
            )
        )

        private fun insertSnapshotSQL(history: String, data: String) = """
            | INSERT INTO  $TABLE_NAME (
            |   aggregate_id,
            |   revision,
            |   history,
            |   data
            | ) VALUES (
            |   '${aggregate.id.get}',
            |   ${aggregate.revisions.current.get},
            |   '$history',
            |   '$data'
            | )
           """.trimMargin()
    }
}
