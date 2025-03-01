package io.github.ustudiocompany.uframework.eventsourcing.event

import io.github.ustudiocompany.uframework.eventsourcing.common.Revision
import io.github.ustudiocompany.uframework.eventsourcing.model.TestEntityId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

internal sealed class TestEvent : AbstractEvent<TestEntityId, TestEvent.Name> {

    enum class Name(override val get: String) : EventName {
        REGISTERED("registered"),
        UPDATED("updated")
    }

    class Registered(
        override val messageId: MessageId,
        override val revision: Revision,
        val data: TestRegistered
    ) : TestEvent() {

        override val aggregateId: TestEntityId
            get() = data.id

        override val name: Name
            get() = Name.REGISTERED
    }

    class Updated(
        override val messageId: MessageId,
        override val revision: Revision,
        val data: TestUpdated
    ) : TestEvent() {

        override val aggregateId: TestEntityId
            get() = data.id

        override val name: Name
            get() = Name.UPDATED
    }
}
