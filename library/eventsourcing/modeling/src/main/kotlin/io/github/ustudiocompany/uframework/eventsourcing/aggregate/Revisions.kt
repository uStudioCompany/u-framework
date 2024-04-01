package io.github.ustudiocompany.uframework.eventsourcing.aggregate

import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.eventsourcing.event.Event
import io.github.ustudiocompany.uframework.eventsourcing.event.EventName

public class Revisions private constructor(
    public val current: Revision,
    public val history: History
) {
    public fun <EVENT, ID, NAME> apply(event: EVENT): Revisions
        where EVENT : Event<ID, NAME>,
              ID : EntityId,
              NAME : EventName = Revisions(current = event.revision, history = history + event)

    public companion object {
        public val initial: Revisions = Revisions(Revision.initial, History.Empty)
        public fun of(current: Revision, history: History): Revisions = Revisions(current, history)
    }
}
