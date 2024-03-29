package com.dream.umbrella.lib.eventsourcing.aggregate

import com.dream.umbrella.lib.eventsourcing.entity.EntityId
import com.dream.umbrella.lib.eventsourcing.event.Event
import com.dream.umbrella.lib.eventsourcing.event.EventName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

@JvmInline
public value class History private constructor(
    private val points: List<Point>
) : Iterable<History.Point> {

    public operator fun <ID, NAME> plus(event: Event<ID, NAME>): History
        where ID : EntityId,
              NAME : EventName = this + Point(event.revision, event.commandId)

    public operator fun get(commandId: MessageId): Revision? =
        points.find { item -> item.commandId == commandId }?.revision

    override fun iterator(): Iterator<Point> = points.iterator()

    public fun maxRevision(): Revision = points.maxBy { point -> point.revision }.revision

    public fun revision(commandId: MessageId): Revision? = points.find { it.commandId == commandId }?.revision

    public data class Point(
        public val revision: Revision,
        public val commandId: MessageId
    )

    public companion object {
        public val Empty: History = History(emptyList())
        public fun of(points: List<Point>): History =
            if (points.isNotEmpty()) History(points) else Empty
    }

    private operator fun plus(other: Point): History = History(points + other)
}
