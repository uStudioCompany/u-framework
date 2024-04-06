package io.github.ustudiocompany.uframework.eventsourcing

import io.github.airflux.functional.Result
import io.github.airflux.functional.kotest.getValue
import io.github.airflux.functional.kotest.shouldBeError
import io.github.airflux.functional.kotest.shouldBeSuccess
import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.eventsourcing.event.TestEvent
import io.github.ustudiocompany.uframework.eventsourcing.event.TestRegistered
import io.github.ustudiocompany.uframework.eventsourcing.event.TestUpdated
import io.github.ustudiocompany.uframework.eventsourcing.model.TestAggregate
import io.github.ustudiocompany.uframework.eventsourcing.model.TestEntityId
import io.github.ustudiocompany.uframework.eventsourcing.store.snapshot.SnapshotStore
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.test.kotest.TestTags
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

internal class EventSourceRepositoryTest : FreeSpec({ tags(TestTags.All, TestTags.Component) }) {

    init {
        "The EventSourceRepository type" - {

            "when snapshot is missing" - {
                val snapshotStore: SnapshotStore<TestAggregate, TestEntityId> = mock {
                    on { loadSnapshot(any<TestEntityId>()) } doReturn Result.asNull
                }

                "when events is missing" - {
                    val eventStore = InMemoryEventStore()

                    "then function `loadAggregate` should return null value" {
                        val repository =
                            EventSourceRepository(snapshotStore, eventStore, TestAggregateFactory())

                        val result = repository.loadAggregate(entityId, 2)
                        val aggregate = result.shouldBeSuccess().value
                        aggregate shouldBe null
                    }
                }

                "when the store contains only an initializing event" - {
                    val initialRevision = Revision.initial
                    val eventStore = InMemoryEventStore(
                        mutableMapOf(
                            entityId to listOf(
                                TestEvent.Registered(
                                    messageId = registerMessageId,
                                    revision = initialRevision,
                                    data = TestRegistered(
                                        id = entityId,
                                        title = INITIAL_TITLE,
                                        description = INITIAL_DESCRIPTION
                                    )
                                )
                            )
                        )
                    )

                    "then function `loadAggregate` should return an aggregate" {
                        val repository =
                            EventSourceRepository(snapshotStore, eventStore, TestAggregateFactory())

                        val result = repository.loadAggregate(entityId, 2)
                        val aggregate = result.shouldBeSuccess().value
                        aggregate.shouldNotBeNull()
                        aggregate.id shouldBe entityId
                        aggregate.entity.id shouldBe entityId
                        aggregate.entity.title shouldBe INITIAL_TITLE
                        aggregate.entity.description shouldBe INITIAL_DESCRIPTION
                        aggregate.history.revision shouldBe initialRevision
                    }
                }

                "when the store contains an initializing and an updating events" - {

                    "when events are ordered" - {
                        val initialRevision = Revision.initial
                        val firstUpdateRevision = initialRevision.next()
                        val secondUpdateRevision = firstUpdateRevision.next()
                        val eventStore = InMemoryEventStore(
                            mutableMapOf(
                                entityId to listOf(
                                    TestEvent.Registered(
                                        messageId = registerMessageId,
                                        revision = initialRevision,
                                        data = TestRegistered(
                                            id = entityId,
                                            title = INITIAL_TITLE,
                                            description = INITIAL_DESCRIPTION
                                        )
                                    ),
                                    TestEvent.Updated(
                                        messageId = firstUpdateMessageId,
                                        revision = firstUpdateRevision,
                                        data = TestUpdated(
                                            id = entityId,
                                            title = UPDATED_TITLE,
                                            description = null
                                        )
                                    ),
                                    TestEvent.Updated(
                                        messageId = secondUpdateMessageId,
                                        revision = secondUpdateRevision,
                                        data = TestUpdated(
                                            id = entityId,
                                            title = null,
                                            description = UPDATED_DESCRIPTION
                                        )
                                    )
                                )
                            )
                        )

                        "then function `loadAggregate` should return an aggregate" {
                            val repository =
                                EventSourceRepository(snapshotStore, eventStore, TestAggregateFactory())

                            val result = repository.loadAggregate(entityId, 2)
                            val aggregate = result.shouldBeSuccess().value
                            aggregate.shouldNotBeNull()
                            aggregate.id shouldBe entityId
                            aggregate.entity.id shouldBe entityId
                            aggregate.entity.title shouldBe UPDATED_TITLE
                            aggregate.entity.description shouldBe UPDATED_DESCRIPTION
                            aggregate.history.revision shouldBe secondUpdateRevision
                        }
                    }

                    "when events are not ordered by revision" - {
                        val initialRevision = Revision.initial
                        val firstUpdateRevision = initialRevision.next()
                        val secondUpdateRevision = firstUpdateRevision.next()
                        val eventStore = InMemoryEventStore(
                            mutableMapOf(
                                entityId to listOf(
                                    TestEvent.Registered(
                                        messageId = registerMessageId,
                                        revision = initialRevision,
                                        data = TestRegistered(
                                            id = entityId,
                                            title = INITIAL_TITLE,
                                            description = INITIAL_DESCRIPTION
                                        )
                                    ),
                                    TestEvent.Updated(
                                        messageId = firstUpdateMessageId,
                                        revision = secondUpdateRevision,
                                        data = TestUpdated(
                                            id = entityId,
                                            title = null,
                                            description = UPDATED_DESCRIPTION
                                        )
                                    ),
                                    TestEvent.Updated(
                                        messageId = secondUpdateMessageId,
                                        revision = firstUpdateRevision,
                                        data = TestUpdated(
                                            id = entityId,
                                            title = UPDATED_TITLE,
                                            description = null
                                        )
                                    )
                                )
                            )
                        )

                        "then function `loadAggregate` should return an error" {
                            val repository =
                                EventSourceRepository(snapshotStore, eventStore, TestAggregateFactory())

                            val result = repository.loadAggregate(entityId, 2)
                            result.shouldBeError().cause
                                .shouldBeInstanceOf<EventSourceRepositoryErrors.Aggregate.Create>()
                        }
                    }

                    "when two events have the same revision" - {
                        val initialRevision = Revision.initial
                        val eventStore = InMemoryEventStore(
                            mutableMapOf(
                                entityId to listOf(
                                    TestEvent.Registered(
                                        messageId = registerMessageId,
                                        revision = initialRevision,
                                        data = TestRegistered(
                                            id = entityId,
                                            title = INITIAL_TITLE,
                                            description = INITIAL_DESCRIPTION
                                        )
                                    ),
                                    TestEvent.Updated(
                                        messageId = firstUpdateMessageId,
                                        revision = initialRevision,
                                        data = TestUpdated(
                                            id = entityId,
                                            title = UPDATED_TITLE,
                                            description = null
                                        )
                                    )
                                )
                            )
                        )

                        "then function `loadAggregate` should return an error" {
                            val repository =
                                EventSourceRepository(snapshotStore, eventStore, TestAggregateFactory())

                            val result = repository.loadAggregate(entityId, 2)
                            result.shouldBeError().cause
                                .shouldBeInstanceOf<EventSourceRepositoryErrors.Aggregate.Create>()
                        }
                    }
                }

                "when the store contains two initializing events" - {
                    val initialRevision = Revision.initial
                    val firstRevision = initialRevision.next()
                    val eventStore = InMemoryEventStore(
                        mutableMapOf(
                            entityId to listOf(
                                TestEvent.Registered(
                                    messageId = registerMessageId,
                                    revision = initialRevision,
                                    data = TestRegistered(
                                        id = entityId,
                                        title = INITIAL_TITLE,
                                        description = INITIAL_DESCRIPTION
                                    )
                                ),
                                TestEvent.Registered(
                                    messageId = registerMessageId,
                                    revision = firstRevision,
                                    data = TestRegistered(
                                        id = entityId,
                                        title = UPDATED_TITLE,
                                        description = null
                                    )
                                )
                            )
                        )
                    )

                    "then function `loadAggregate` should return an error" {
                        val repository =
                            EventSourceRepository(snapshotStore, eventStore, TestAggregateFactory())

                        val result = repository.loadAggregate(entityId, 2)
                        result.shouldBeError().cause
                            .shouldBeInstanceOf<EventSourceRepositoryErrors.Aggregate.Create>()
                    }
                }
            }
        }
    }

    companion object {
        private const val ENTITY_ID = "3bb5b407-ae3c-4386-b09e-61977769a518"
        private const val REGISTER_MESSAGE_ID = "8d164913-4aa4-4fcf-aba8-a9beaaae48f1"
        private const val FIRST_UPDATE_MESSAGE_ID = "1542b339-2f80-4bf4-bd95-b7cddd512483"
        private const val SECOND_UPDATE_MESSAGE_ID = "a8a73b5d-5951-4eb3-bcc3-d5d61f23d60e"

        private val entityId = TestEntityId(ENTITY_ID)
        private val registerMessageId = MessageId.of(REGISTER_MESSAGE_ID).getValue()
        private val firstUpdateMessageId = MessageId.of(FIRST_UPDATE_MESSAGE_ID).getValue()
        private val secondUpdateMessageId = MessageId.of(SECOND_UPDATE_MESSAGE_ID).getValue()

        private const val INITIAL_TITLE = "title-1"
        private const val UPDATED_TITLE = "title-2"

        private const val INITIAL_DESCRIPTION = "description-1"
        private const val UPDATED_DESCRIPTION = "description-2"
    }
}
