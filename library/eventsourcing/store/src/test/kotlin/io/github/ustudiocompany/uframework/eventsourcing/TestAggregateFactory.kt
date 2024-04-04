package io.github.ustudiocompany.uframework.eventsourcing

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.mapError
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.AggregateFactory
import io.github.ustudiocompany.uframework.eventsourcing.event.TestEvent
import io.github.ustudiocompany.uframework.eventsourcing.model.TestAggregate
import io.github.ustudiocompany.uframework.eventsourcing.model.TestEntityId
import io.github.ustudiocompany.uframework.eventsourcing.model.applyEvent
import io.github.ustudiocompany.uframework.failure.Failure

internal class TestAggregateFactory : AggregateFactory<TestAggregate, TestEntityId, TestEvent, TestEvent.Name> {

    override fun apply(aggregate: TestAggregate?, event: TestEvent): Result<TestAggregate, Errors> =
        when (event) {
            is TestEvent.Registered -> if (aggregate == null)
                TestAggregate.applyEvent(event).mapError { Errors.ApplyEvent(event) }
            else
                Errors.UnexpectedEvent(event).error()

            is TestEvent.Updated -> if (aggregate != null)
                aggregate.applyEvent(event).mapError { Errors.ApplyEvent(event) }
            else
                Errors.UnexpectedEvent(event).error()
        }

    internal sealed class Errors : Failure {
        override val domain: String = "TEST-AGGREGATE-FACTORY"

        class UnexpectedEvent(private val event: TestEvent) : Errors() {
            override val number: String = "1"
            override val description: String = "The unexpected event."
        }

        class ApplyEvent(private val event: TestEvent) : Errors() {
            override val number: String = "2"
            override val description: String = "The applying event error."
        }
    }
}
