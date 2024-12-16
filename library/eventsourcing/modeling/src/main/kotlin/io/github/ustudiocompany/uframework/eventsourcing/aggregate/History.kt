package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

@JvmInline
public value class History private constructor(
    public val events: List<Event>
) {

    public val revision: Revision
        get() = events.last().revision

    public fun add(revision: Revision, messageId: MessageId): ResultK<History, Errors> =
        plus(Event(revision, messageId))

    public operator fun plus(event: Event): ResultK<History, Errors> {
        fun checkUniqueMessageId(messageId: MessageId): Errors.NonUniqueMessageId? =
            events.find { item -> item.messageId == messageId }
                ?.let { Errors.NonUniqueMessageId(messageId) }

        val error = compareRevisions(current = revision, next = event.revision)
            ?: checkUniqueMessageId(messageId = event.messageId)
        if (error != null) return error.asFailure()

        return History(events + event).asSuccess()
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

        public fun of(revision: Revision, messageId: MessageId): ResultK<History, Errors> =
            of(Event(revision = revision, messageId = messageId))

        public fun of(event: Event): ResultK<History, Errors> = of(listOf(event))

        public fun of(events: List<Event>): ResultK<History, Errors> =
            if (events.isNotEmpty())
                events.checkEvents()?.asFailure()
                    ?: History(events).asSuccess()
            else
                Errors.HistoryIsEmpty.asFailure()

        private fun List<Event>.checkEvents(): Errors? {
            fun List<Event>.checkRevisionFirstEvent(): Errors.InvalidRevision? {
                val revision = first().revision
                return if (revision != Revision.INITIAL)
                    Errors.InvalidRevision(expected = Revision.INITIAL, actual = revision)
                else
                    null
            }

            tailrec fun List<Event>.checkRevisions(index: Int): Errors.InvalidRevision? {
                if (index + 1 == this.size) return null
                val current = this[index].revision
                val next = this[index + 1].revision
                val error = compareRevisions(current, next)
                return if (error != null)
                    error
                else
                    checkRevisions(index + 1)
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

            checkRevisionFirstEvent()?.let { return it }

            return if (size == 1)
                null
            else
                checkRevisions(0) ?: checkUniqueMessageId()
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
