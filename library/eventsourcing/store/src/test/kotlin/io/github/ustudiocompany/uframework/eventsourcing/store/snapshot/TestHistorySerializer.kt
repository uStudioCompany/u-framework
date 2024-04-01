package io.github.ustudiocompany.uframework.eventsourcing.store.snapshot

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.functional.Result
import io.github.airflux.functional.orThrow
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.History
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Revision
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

internal class TestHistorySerializer(private val mapper: ObjectMapper) : HistorySerializer<String> {

    override fun serialize(history: History): String =
        mapper.writeValueAsString(
            HistoryData(history.map { Point(it.revision.get, it.commandId.get) })
        )

    override fun deserialize(value: String): Result<History, Failure> =
        History.of(
            points = mapper.readValue(value, HistoryData::class.java)
                .map {
                    History.Point(
                        Revision.of(it.revision).orThrow { IllegalArgumentException("Invalid revision") },
                        MessageId.of(it.commandId).orThrow { IllegalArgumentException("Invalid commandId") }
                    )
                }
        ).success()

    private class HistoryData @JsonCreator constructor(
        private val points: List<Point>
    ) : List<Point> by points

    private data class Point(
        @JsonProperty("revision") val revision: Long,
        @JsonProperty("commandId") val commandId: String
    )
}
