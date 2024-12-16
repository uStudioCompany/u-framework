package io.github.ustudiocompany.uframework.eventsourcing

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
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
import io.kotest.matchers.nulls.shouldBeNull
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
                    on { loadSnapshot(any<TestEntityId>()) } doReturn Success.asNull
                }

                "when events is missing" - {
                    val eventStore = InMemoryEventStore()

                    "then function `loadAggregate` should return null value" {
                        val repository =
                            EventSourceRepository(snapshotStore, eventStore, TestAggregateFactory())

                        val result = repository.loadAggregate(ENTITY_ID_VALUE, 2)
                        result.shouldBeSuccess()
                        result.value.shouldBeNull()
                    }
                }

                "when the store contains only an initializing event" - {
                    val initialRevision = Revision.INITIAL
                    val eventStore = InMemoryEventStore(
                        mutableMapOf(
                            ENTITY_ID_VALUE to listOf(
                                TestEvent.Registered(
                                    messageId = REGISTER_MESSAGE_ID_VALUE,
                                    revision = initialRevision,
                                    data = TestRegistered(
                                        id = ENTITY_ID_VALUE,
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

                        val result = repository.loadAggregate(ENTITY_ID_VALUE, 2)
                        result.shouldBeSuccess()
                        val aggregate = result.value
                        aggregate.shouldNotBeNull()
                        aggregate.id shouldBe ENTITY_ID_VALUE
                        aggregate.entity.id shouldBe ENTITY_ID_VALUE
                        aggregate.entity.title shouldBe INITIAL_TITLE
                        aggregate.entity.description shouldBe INITIAL_DESCRIPTION
                        aggregate.history.revision shouldBe initialRevision
                    }
                }

                "when the store contains an initializing and an updating events" - {

                    "when events are ordered" - {
                        val initialRevision = Revision.INITIAL
                        val firstUpdateRevision = initialRevision.next()
                        val secondUpdateRevision = firstUpdateRevision.next()
                        val eventStore = InMemoryEventStore(
                            mutableMapOf(
                                ENTITY_ID_VALUE to listOf(
                                    TestEvent.Registered(
                                        messageId = REGISTER_MESSAGE_ID_VALUE,
                                        revision = initialRevision,
                                        data = TestRegistered(
                                            id = ENTITY_ID_VALUE,
                                            title = INITIAL_TITLE,
                                            description = INITIAL_DESCRIPTION
                                        )
                                    ),
                                    TestEvent.Updated(
                                        messageId = FIRST_UPDATE_MESSAGE_ID_VALUE,
                                        revision = firstUpdateRevision,
                                        data = TestUpdated(
                                            id = ENTITY_ID_VALUE,
                                            title = UPDATED_TITLE,
                                            description = null
                                        )
                                    ),
                                    TestEvent.Updated(
                                        messageId = SECOND_UPDATE_MESSAGE_ID_VALUE,
                                        revision = secondUpdateRevision,
                                        data = TestUpdated(
                                            id = ENTITY_ID_VALUE,
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

                            val result = repository.loadAggregate(ENTITY_ID_VALUE, 2)
                            result.shouldBeSuccess()
                            val aggregate = result.value
                            aggregate.shouldNotBeNull()
                            aggregate.id shouldBe ENTITY_ID_VALUE
                            aggregate.entity.id shouldBe ENTITY_ID_VALUE
                            aggregate.entity.title shouldBe UPDATED_TITLE
                            aggregate.entity.description shouldBe UPDATED_DESCRIPTION
                            aggregate.history.revision shouldBe secondUpdateRevision
                        }
                    }

                    "when events are not ordered by revision" - {
                        val initialRevision = Revision.INITIAL
                        val firstUpdateRevision = initialRevision.next()
                        val secondUpdateRevision = firstUpdateRevision.next()
                        val eventStore = InMemoryEventStore(
                            mutableMapOf(
                                ENTITY_ID_VALUE to listOf(
                                    TestEvent.Registered(
                                        messageId = REGISTER_MESSAGE_ID_VALUE,
                                        revision = initialRevision,
                                        data = TestRegistered(
                                            id = ENTITY_ID_VALUE,
                                            title = INITIAL_TITLE,
                                            description = INITIAL_DESCRIPTION
                                        )
                                    ),
                                    TestEvent.Updated(
                                        messageId = FIRST_UPDATE_MESSAGE_ID_VALUE,
                                        revision = secondUpdateRevision,
                                        data = TestUpdated(
                                            id = ENTITY_ID_VALUE,
                                            title = null,
                                            description = UPDATED_DESCRIPTION
                                        )
                                    ),
                                    TestEvent.Updated(
                                        messageId = SECOND_UPDATE_MESSAGE_ID_VALUE,
                                        revision = firstUpdateRevision,
                                        data = TestUpdated(
                                            id = ENTITY_ID_VALUE,
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

                            val result = repository.loadAggregate(ENTITY_ID_VALUE, 2)
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<EventSourceRepositoryErrors.Aggregate.Create>()
                        }
                    }

                    "when two events have the same revision" - {
                        val initialRevision = Revision.INITIAL
                        val eventStore = InMemoryEventStore(
                            mutableMapOf(
                                ENTITY_ID_VALUE to listOf(
                                    TestEvent.Registered(
                                        messageId = REGISTER_MESSAGE_ID_VALUE,
                                        revision = initialRevision,
                                        data = TestRegistered(
                                            id = ENTITY_ID_VALUE,
                                            title = INITIAL_TITLE,
                                            description = INITIAL_DESCRIPTION
                                        )
                                    ),
                                    TestEvent.Updated(
                                        messageId = FIRST_UPDATE_MESSAGE_ID_VALUE,
                                        revision = initialRevision,
                                        data = TestUpdated(
                                            id = ENTITY_ID_VALUE,
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

                            val result = repository.loadAggregate(ENTITY_ID_VALUE, 2)
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<EventSourceRepositoryErrors.Aggregate.Create>()
                        }
                    }
                }

                "when the store contains two initializing events" - {
                    val initialRevision = Revision.INITIAL
                    val firstRevision = initialRevision.next()
                    val eventStore = InMemoryEventStore(
                        mutableMapOf(
                            ENTITY_ID_VALUE to listOf(
                                TestEvent.Registered(
                                    messageId = REGISTER_MESSAGE_ID_VALUE,
                                    revision = initialRevision,
                                    data = TestRegistered(
                                        id = ENTITY_ID_VALUE,
                                        title = INITIAL_TITLE,
                                        description = INITIAL_DESCRIPTION
                                    )
                                ),
                                TestEvent.Registered(
                                    messageId = REGISTER_MESSAGE_ID_VALUE,
                                    revision = firstRevision,
                                    data = TestRegistered(
                                        id = ENTITY_ID_VALUE,
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

                        val result = repository.loadAggregate(ENTITY_ID_VALUE, 2)
                        result.shouldBeFailure()
                        result.cause.shouldBeInstanceOf<EventSourceRepositoryErrors.Aggregate.Create>()
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

        private val ENTITY_ID_VALUE = TestEntityId(ENTITY_ID)
        private val REGISTER_MESSAGE_ID_VALUE = (MessageId.of(REGISTER_MESSAGE_ID) as Success).value
        private val FIRST_UPDATE_MESSAGE_ID_VALUE = (MessageId.of(FIRST_UPDATE_MESSAGE_ID) as Success).value
        private val SECOND_UPDATE_MESSAGE_ID_VALUE = (MessageId.of(SECOND_UPDATE_MESSAGE_ID) as Success).value

        private const val INITIAL_TITLE = "title-1"
        private const val UPDATED_TITLE = "title-2"

        private const val INITIAL_DESCRIPTION = "description-1"
        private const val UPDATED_DESCRIPTION = "description-2"
    }
}
