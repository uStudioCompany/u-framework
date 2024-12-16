package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.test.kotest.TestTags
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class HistoryTest : FreeSpec({ tags(TestTags.All, TestTags.Unit) }) {

    init {

        "The History type" - {

            "the factory method of creating" - {

                "when events is empty" - {
                    val events = emptyList<History.Event>()

                    "then the factory method should return null value" {
                        val result = History.of(events)
                        result.shouldBeFailure()
                        val error = result.cause
                        error.shouldBeInstanceOf<History.Errors.HistoryIsEmpty>()
                    }
                }

                "when events contains only one item" - {

                    "when the revision of this item equal to `initialize`" - {
                        val events = listOf(History.Event(INITIAL_REVISION, FIRST_MESSAGE_ID_VALUE))

                        "then the factory method should return the revision value" - {
                            val result = History.of(events)

                            "then the current revision should equal to `initialize`" {
                                result.shouldBeSuccess()
                                val history = result.value
                                history.revision shouldBe Revision.INITIAL
                            }
                        }
                    }

                    "when the revision of this item is not equal to `initialize`" - {
                        val events = listOf(History.Event(SECOND_REVISION, FIRST_MESSAGE_ID_VALUE))

                        "then the factory method should return an error" {
                            val result = History.of(events)
                            result.shouldBeFailure()
                            val error = result.cause
                            error.shouldBeInstanceOf<History.Errors.InvalidRevision>()
                            error.expected shouldBe INITIAL_REVISION
                            error.actual shouldBe SECOND_REVISION
                        }
                    }
                }

                "when events contains some items" - {

                    "when the revision of first item equal to `initialize`" - {
                        val firstRevision = INITIAL_REVISION

                        "when revisions of history elements are successively increasing" - {

                            "when a message id is unique" - {
                                val events = listOf(
                                    History.Event(firstRevision, FIRST_MESSAGE_ID_VALUE),
                                    History.Event(SECOND_REVISION, SECOND_MESSAGE_ID_VALUE)
                                )

                                "then the factory method should return the revision value" - {
                                    val result = History.of(events)

                                    "then the current revision is equal to the revision of the last history item" {
                                        result.shouldBeSuccess()
                                        val history = result.value
                                        history.shouldNotBeNull()
                                        history.revision shouldBe SECOND_REVISION
                                    }
                                }
                            }

                            "when a message id is non-unique" - {
                                val events = listOf(
                                    History.Event(firstRevision, FIRST_MESSAGE_ID_VALUE),
                                    History.Event(SECOND_REVISION, FIRST_MESSAGE_ID_VALUE)
                                )

                                "then the factory method should return an error" - {
                                    val result = History.of(events)
                                    result.shouldBeFailure()
                                    val error = result.cause
                                    error.shouldBeInstanceOf<History.Errors.NonUniqueMessageId>()
                                    error.id shouldBe FIRST_MESSAGE_ID_VALUE
                                }
                            }
                        }

                        "when revisions of history elements are not consistently increased" - {
                            val events = listOf(
                                History.Event(firstRevision, FIRST_MESSAGE_ID_VALUE),
                                History.Event(THIRD_REVISION, SECOND_MESSAGE_ID_VALUE)
                            )

                            "then the factory method should return an error" {
                                val result = History.of(events)
                                result.shouldBeFailure()
                                val error = result.cause
                                error.shouldBeInstanceOf<History.Errors.InvalidRevision>()
                                error.expected shouldBe SECOND_REVISION
                                error.actual shouldBe THIRD_REVISION
                            }
                        }
                    }

                    "when the revision of first item is not equal to `initialize`" - {
                        val firstRevision = SECOND_REVISION
                        val events = listOf(
                            History.Event(firstRevision, FIRST_MESSAGE_ID_VALUE),
                            History.Event(SECOND_REVISION, SECOND_MESSAGE_ID_VALUE)
                        )

                        "then the factory method should return an error" {
                            val result = History.of(events)
                            result.shouldBeFailure()
                            val error = result.cause
                            error.shouldBeInstanceOf<History.Errors.InvalidRevision>()
                            error.expected shouldBe INITIAL_REVISION
                            error.actual shouldBe SECOND_REVISION
                        }
                    }
                }
            }

            "the function appending a new history event" - {
                val history = (History.of(
                    listOf(
                        History.Event(INITIAL_REVISION, FIRST_MESSAGE_ID_VALUE),
                        History.Event(SECOND_REVISION, SECOND_MESSAGE_ID_VALUE)
                    )
                ) as Success).value

                "when a new history event contains an unique message id" - {
                    val result = history.add(revision = THIRD_REVISION, messageId = THIRD_MESSAGE_ID_VALUE)

                    "then the function should return new revisions" {
                        result.shouldBeSuccess()
                        val newHistory = result.value
                        newHistory.revision shouldBe THIRD_REVISION
                    }
                }

                "when a new history event contains a non-unique message id" - {
                    val result = history.add(revision = THIRD_REVISION, messageId = SECOND_MESSAGE_ID_VALUE)

                    "then the function should return an error" {
                        result.shouldBeFailure()
                        val error = result.cause
                        error.shouldBeInstanceOf<History.Errors.NonUniqueMessageId>()
                        error.id shouldBe SECOND_MESSAGE_ID_VALUE
                    }
                }
            }

            "the function searching for a revision by message id" - {
                val history = (History.of(
                    listOf(
                        History.Event(INITIAL_REVISION, FIRST_MESSAGE_ID_VALUE),
                        History.Event(SECOND_REVISION, SECOND_MESSAGE_ID_VALUE)
                    )
                ) as Success).value

                "when history contains the message id" - {

                    "then the function should return new revisions" {
                        val revision = history[SECOND_MESSAGE_ID_VALUE]
                        revision.shouldNotBeNull()
                        revision shouldBe SECOND_REVISION
                    }
                }

                "when history not contain the message id" - {

                    "then the function should return null value" {
                        val revision = history[THIRD_MESSAGE_ID_VALUE]
                        revision.shouldBeNull()
                    }
                }
            }
        }
    }

    companion object {

        private const val FIRST_MESSAGE_ID = "56c0e9f0-fa9b-483a-91f2-f801b777cf50"
        private const val SECOND_MESSAGE_ID = "a56e8973-5802-4063-a964-5ae381cff0c3"
        private const val THIRD_MESSAGE_ID = "01bfd084-35c3-4916-bba2-f87f8b750b3d"

        private val INITIAL_REVISION = Revision.INITIAL
        private val SECOND_REVISION = INITIAL_REVISION.next()
        private val THIRD_REVISION = SECOND_REVISION.next()

        private val FIRST_MESSAGE_ID_VALUE = (MessageId.of(FIRST_MESSAGE_ID) as Success).value
        private val SECOND_MESSAGE_ID_VALUE = (MessageId.of(SECOND_MESSAGE_ID) as Success).value
        private val THIRD_MESSAGE_ID_VALUE = (MessageId.of(THIRD_MESSAGE_ID) as Success).value
    }
}
