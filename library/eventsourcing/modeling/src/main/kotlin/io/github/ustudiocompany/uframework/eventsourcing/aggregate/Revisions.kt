package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.eventsourcing.event.EventName
import io.github.ustudiocompany.uframework.failure.Failure

public class Revisions private constructor(
    public val current: Revision,
    public val history: History
) {
    public fun <EVENT, ID, NAME> apply(event: EVENT): Result<Revisions, Errors>
        where EVENT : Event<ID, NAME>,
              ID : EntityId,
              NAME : EventName =
        compareRevisions(current = current, next = event.revision)?.error()
            ?: Revisions(current = event.revision, history = history + event).success()

    public companion object {
        public val initial: Revisions = Revisions(Revision.initial, History.Empty)
        public fun of(current: Revision, history: History): Revisions = Revisions(current, history)

        private fun compareRevisions(current: Revision, next: Revision): Errors.InvalidRevision? {
            val expected = current.next()
            return if (expected == next)
                null
            else
                Errors.InvalidRevision(expected = expected, actual = next)
        }
    }

    public sealed class Errors : Failure {
        override val domain: String = "REVISIONS"

        public class InvalidRevision(expected: Revision, actual: Revision) : Errors() {
            override val number: String = "1"
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
    }
}
