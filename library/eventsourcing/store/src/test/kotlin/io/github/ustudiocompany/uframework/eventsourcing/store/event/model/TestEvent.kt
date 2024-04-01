package io.github.ustudiocompany.uframework.eventsourcing.store.event.model

import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revision
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.eventsourcing.event.EventName
import io.github.ustudiocompany.uframework.eventsourcing.store.model.TestEntityId
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

public sealed class TestEvent : Event<TestEntityId, TestEvent.Name>() {

    public enum class Name(override val get: String) : EventName {
        REGISTERED("registered"),
        UPDATED("updated")
    }

    public class Registered(
        override val commandId: MessageId,
        override val correlationId: CorrelationId,
        override val revision: Revision,
        public val data: TestRegistered
    ) : TestEvent() {

        override val aggregateId: TestEntityId
            get() = data.id

        override val name: Name
            get() = Name.REGISTERED
    }

    public class Updated(
        override val commandId: MessageId,
        override val correlationId: CorrelationId,
        override val revision: Revision,
        public val data: TestUpdated
    ) : TestEvent() {

        override val aggregateId: TestEntityId
            get() = data.id

        override val name: Name
            get() = Name.UPDATED
    }
}
