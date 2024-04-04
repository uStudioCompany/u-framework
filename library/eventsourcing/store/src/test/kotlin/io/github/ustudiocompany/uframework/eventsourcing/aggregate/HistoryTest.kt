package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.airflux.functional.kotest.getValue
import io.github.airflux.functional.kotest.shouldBeError
import io.github.airflux.functional.kotest.shouldBeSuccess
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

                "when points is empty" - {
                    val points = emptyList<History.Point>()

                    "then the factory method should return null value" {
                        val result = History.of(points)
                        result.shouldBeError().cause
                            .shouldBeInstanceOf<History.Errors.HistoryIsEmpty>()
                    }
                }

                "when points contains only one item" - {

                    "when the revision of this item equal to `initialize`" - {
                        val points = listOf(History.Point(initialRevision, firstMessageID))

                        "then the factory method should return the revision value" - {
                            val history = History.of(points).shouldBeSuccess().value

                            "then the current revision should equal to `initialize`" {
                                history.revision shouldBe Revision.initial
                            }
                        }
                    }

                    "when the revision of this item is not equal to `initialize`" - {
                        val points = listOf(History.Point(secondRevision, firstMessageID))

                        "then the factory method should return an error" {
                            val error = History.of(points).shouldBeError().cause
                                .shouldBeInstanceOf<History.Errors.InvalidRevision>()
                            error.expected shouldBe initialRevision
                            error.actual shouldBe secondRevision
                        }
                    }
                }

                "when points contains some items" - {

                    "when the revision of first item equal to `initialize`" - {
                        val firstRevision = initialRevision

                        "when revisions of history elements are successively increasing" - {

                            "when a message id is unique" - {
                                val points = listOf(
                                    History.Point(firstRevision, firstMessageID),
                                    History.Point(secondRevision, secondMessageID)
                                )

                                "then the factory method should return the revision value" - {
                                    val history = History.of(points).shouldBeSuccess().value

                                    "then the current revision is equal to the revision of the last history item" {
                                        history.shouldNotBeNull()
                                        history.revision shouldBe secondRevision
                                    }
                                }
                            }

                            "when a message id is non-unique" - {
                                val points = listOf(
                                    History.Point(firstRevision, firstMessageID),
                                    History.Point(secondRevision, firstMessageID)
                                )

                                "then the factory method should return an error" - {
                                    val error = History.of(points).shouldBeError().cause
                                        .shouldBeInstanceOf<History.Errors.NonUniqueMessageId>()
                                    error.id shouldBe firstMessageID
                                }
                            }
                        }

                        "when revisions of history elements are not consistently increased" - {
                            val points = listOf(
                                History.Point(firstRevision, firstMessageID),
                                History.Point(thirdRevision, secondMessageID)
                            )

                            "then the factory method should return an error" {
                                val error = History.of(points).shouldBeError().cause
                                    .shouldBeInstanceOf<History.Errors.InvalidRevision>()
                                error.expected shouldBe secondRevision
                                error.actual shouldBe thirdRevision
                            }
                        }
                    }

                    "when the revision of first item is not equal to `initialize`" - {
                        val firstRevision = secondRevision
                        val points = listOf(
                            History.Point(firstRevision, firstMessageID),
                            History.Point(secondRevision, secondMessageID)
                        )

                        "then the factory method should return an error" {
                            val error = History.of(points).shouldBeError().cause
                                .shouldBeInstanceOf<History.Errors.InvalidRevision>()
                            error.expected shouldBe initialRevision
                            error.actual shouldBe secondRevision
                        }
                    }
                }
            }

            "the function appending a new history point" - {
                val history = History.of(
                    listOf(
                        History.Point(initialRevision, firstMessageID),
                        History.Point(secondRevision, secondMessageID)
                    )
                ).getValue()

                "when a new history point contains an unique message id" - {
                    val result = history.add(revision = thirdRevision, commandId = thirdMessageID)

                    "then the function should return new revisions" {
                        result.shouldBeSuccess().value.revision shouldBe thirdRevision
                    }
                }

                "when a new history point contains a non-unique message id" - {
                    val result = history.add(revision = thirdRevision, commandId = secondMessageID)

                    "then the function should return an error" {
                        val error = result.shouldBeError().cause
                            .shouldBeInstanceOf<History.Errors.NonUniqueMessageId>()
                        error.id shouldBe secondMessageID
                    }
                }
            }

            "the function searching for a revision by message id" - {
                val history = History.of(
                    listOf(
                        History.Point(initialRevision, firstMessageID),
                        History.Point(secondRevision, secondMessageID)
                    )
                ).getValue()

                "when history contains the message id" - {

                    "then the function should return new revisions" {
                        val revision = history[secondMessageID]
                        revision.shouldNotBeNull()
                        revision shouldBe secondRevision
                    }
                }

                "when history not contain the message id" - {

                    "then the function should return null value" {
                        val revision = history[thirdMessageID]
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

        private val initialRevision = Revision.initial
        private val secondRevision = initialRevision.next()
        private val thirdRevision = secondRevision.next()

        private val firstMessageID = MessageId.of(FIRST_MESSAGE_ID).getValue()
        private val secondMessageID = MessageId.of(SECOND_MESSAGE_ID).getValue()
        private val thirdMessageID = MessageId.of(THIRD_MESSAGE_ID).getValue()
    }
}
