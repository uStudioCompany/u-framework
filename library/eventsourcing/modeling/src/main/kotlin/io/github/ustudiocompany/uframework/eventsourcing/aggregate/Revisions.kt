package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

@JvmInline
public value class Revisions private constructor(
    public val history: List<Point>
) {

    public val current: Revision
        get() = history.last().revision

    public fun add(revision: Revision, commandId: MessageId): Result<Revisions, Errors> =
        plus(Point(revision, commandId))

    public operator fun plus(point: Point): Result<Revisions, Errors> {
        fun checkUniqueMessageId(commandId: MessageId): Errors.NonUniqueMessageId? =
            if (history.any { item -> item.commandId == commandId })
                Errors.NonUniqueMessageId(commandId)
            else
                null

        val error = compareRevisions(current = current, next = point.revision)
            ?: checkUniqueMessageId(commandId = point.commandId)
        if (error != null) return error.error()

        return Revisions(history + point).success()
    }

    public operator fun get(commandId: MessageId): Revision? =
        history.find { item -> item.commandId == commandId }?.revision

    public data class Point(
        public val revision: Revision,
        public val commandId: MessageId
    )

    public sealed class Errors : Failure {
        override val domain: String = "REVISIONS"

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

        public fun of(revision: Revision, messageId: MessageId): Result<Revisions, Errors> =
            of(Point(revision = revision, commandId = messageId))

        public fun of(point: Point): Result<Revisions, Errors> =
            of(listOf(point))

        public fun of(history: List<Point>): Result<Revisions, Errors> =
            if (history.isNotEmpty())
                checkHistory(history)?.error()
                    ?: Revisions(history).success()
            else
                Errors.HistoryIsEmpty.error()

        private fun checkHistory(history: List<Point>): Errors? {
            tailrec fun checkRevisions(history: List<Point>, index: Int): Errors.InvalidRevision? {
                if (index + 1 == history.size) return null
                val current = history[index].revision
                val next = history[index + 1].revision
                return compareRevisions(current, next) ?: checkRevisions(history, index + 1)
            }

            fun checkUniqueMessageId(history: List<Point>): Errors.NonUniqueMessageId? {
                mutableSetOf<MessageId>().apply {
                    history.forEach { item ->
                        if (!add(item.commandId))
                            return Errors.NonUniqueMessageId(item.commandId)
                    }
                }
                return null
            }

            val revision = history.first().revision
            if (revision != Revision.initial)
                return Errors.InvalidRevision(expected = Revision.initial, actual = revision)

            return if (history.size == 1)
                null
            else
                checkRevisions(history, 0) ?: checkUniqueMessageId(history)
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
