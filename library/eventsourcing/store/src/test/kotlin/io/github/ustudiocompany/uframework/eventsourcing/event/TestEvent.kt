package io.github.ustudiocompany.uframework.eventsourcing.event

import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.eventsourcing.model.TestEntityId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

public sealed class TestEvent : AbstractEvent<TestEntityId, TestEvent.Name> {

    public enum class Name(override val get: String) : EventName {
        REGISTERED("registered"),
        UPDATED("updated")
    }

    public class Registered(
        override val messageId: MessageId,
        override val revision: Revision,
        public val data: TestRegistered
    ) : TestEvent() {

        override val aggregateId: TestEntityId
            get() = data.id

        override val name: Name
            get() = Name.REGISTERED
    }

    public class Updated(
        override val messageId: MessageId,
        override val revision: Revision,
        public val data: TestUpdated
    ) : TestEvent() {

        override val aggregateId: TestEntityId
            get() = data.id

        override val name: Name
            get() = Name.UPDATED
    }
}
