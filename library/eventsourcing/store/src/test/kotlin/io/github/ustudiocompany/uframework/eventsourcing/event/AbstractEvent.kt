package io.github.ustudiocompany.uframework.eventsourcing.event

import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId

internal interface AbstractEvent<out ID, out NAME> : Event<ID>
    where ID : EntityId,
          NAME : EventName {

    val name: NAME
}
