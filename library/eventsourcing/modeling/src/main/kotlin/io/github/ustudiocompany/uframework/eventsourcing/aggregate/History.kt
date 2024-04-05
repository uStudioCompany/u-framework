package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

@JvmInline
public value class History private constructor(
    public val events: List<Event>
) {

    public val revision: Revision
        get() = events.last().revision

    public fun add(revision: Revision, messageId: MessageId): Result<History, Errors> =
        plus(Event(revision, messageId))

    public operator fun plus(event: Event): Result<History, Errors> {
        fun checkUniqueMessageId(messageId: MessageId): Errors.NonUniqueMessageId? =
            events.find { item -> item.messageId == messageId }
                ?.let { Errors.NonUniqueMessageId(messageId) }

        val error = compareRevisions(current = revision, next = event.revision)
            ?: checkUniqueMessageId(messageId = event.messageId)
        if (error != null) return error.error()

        return History(events + event).success()
    }

    public operator fun get(messageId: MessageId): Revision? =
        events.find { item -> item.messageId == messageId }?.revision

    public data class Event(
        public val revision: Revision,
        public val messageId: MessageId
    )

    public sealed class Errors : Failure {
        override val domain: String = "HISTORY"

        public data object HistoryIsEmpty : Errors() {
            override val number: String = "1"
            override val description: String = "The history is empty."
        }

        public class InvalidRevision(public val expected: Revision, public val actual: Revision) : Errors() {
            override val number: String = "2"
            override val description: String =
                "Invalid revision. Expected: `${expected.get}`, actual: `${actual.get}`"
            override val details: Failure.Details = Failure.Details.of(
                EXPECTED_DETAILS_KEY to expected.get.toString(),
                ACTUAL_DETAILS_KEY to actual.get.toString()
            )

            private companion object {
                private const val EXPECTED_DETAILS_KEY = "expected-revision"
                private const val ACTUAL_DETAILS_KEY = "actual-revision"
            }
        }

        public class NonUniqueMessageId(public val id: MessageId) : Errors() {
            override val number: String = "3"
            override val description: String =
                "Non-unique message id. The message id: `${id.get}`"
            override val details: Failure.Details = Failure.Details.of(
                MESSAGE_ID_DETAILS_KEY to id.get
            )

            private companion object {
                private const val MESSAGE_ID_DETAILS_KEY = "message-id"
            }
        }
    }

    public companion object {

        public fun of(revision: Revision, messageId: MessageId): Result<History, Errors> =
            of(Event(revision = revision, messageId = messageId))

        public fun of(event: Event): Result<History, Errors> = of(listOf(event))

        public fun of(events: List<Event>): Result<History, Errors> =
            if (events.isNotEmpty())
                checkEvents(events)?.error()
                    ?: History(events).success()
            else
                Errors.HistoryIsEmpty.error()

        private fun checkEvents(events: List<Event>): Errors? {
            fun List<Event>.checkRevisionFirstEvent(): Errors.InvalidRevision? {
                val revision = first().revision
                return if (revision != Revision.initial)
                    Errors.InvalidRevision(expected = Revision.initial, actual = revision)
                else
                    null
            }

            tailrec fun List<Event>.checkRevisions(index: Int): Errors.InvalidRevision? {
                if (index + 1 == this.size) return null
                val current = this[index].revision
                val next = this[index + 1].revision
                return compareRevisions(current, next) ?: checkRevisions(index + 1)
            }

            fun List<Event>.checkUniqueMessageId(): Errors.NonUniqueMessageId? {
                mutableSetOf<MessageId>().apply {
                    this@checkUniqueMessageId.forEach { item ->
                        if (!add(item.messageId))
                            return Errors.NonUniqueMessageId(item.messageId)
                    }
                }
                return null
            }

            events.checkRevisionFirstEvent()?.let { return it }

            return if (events.size == 1)
                null
            else
                events.checkRevisions(0) ?: events.checkUniqueMessageId()
        }

        private fun compareRevisions(current: Revision, next: Revision): Errors.InvalidRevision? {
            val expected = current.next()
            return if (expected == next)
                null
            else
                Errors.InvalidRevision(expected = expected, actual = next)
        }
    }
}
