package io.github.ustudiocompany.uframework.eventsourcing

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.mapFailure
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.AggregateFactory
import io.github.ustudiocompany.uframework.eventsourcing.event.TestEvent
import io.github.ustudiocompany.uframework.eventsourcing.model.TestAggregate
import io.github.ustudiocompany.uframework.eventsourcing.model.TestEntityId
import io.github.ustudiocompany.uframework.eventsourcing.model.applyEvent
import io.github.ustudiocompany.uframework.failure.Failure

internal class TestAggregateFactory : AggregateFactory<TestAggregate, TestEntityId, TestEvent> {

    override fun apply(aggregate: TestAggregate?, event: TestEvent): Result<TestAggregate, Errors> =
        when (event) {
            is TestEvent.Registered -> if (aggregate == null)
                TestAggregate.applyEvent(event).mapFailure { Errors.ApplyEvent(event) }
            else
                Errors.UnexpectedEvent(event).failure()

            is TestEvent.Updated -> if (aggregate != null)
                aggregate.applyEvent(event).mapFailure { Errors.ApplyEvent(event) }
            else
                Errors.UnexpectedEvent(event).failure()
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
