package io.github.ustudiocompany.uframework.eventsourcing.store.snapshot

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.eventsourcing.EventSourceRepositoryErrors
import io.github.ustudiocompany.uframework.eventsourcing.store.model.TestAggregate
import io.github.ustudiocompany.uframework.eventsourcing.store.model.TestEntity
import io.github.ustudiocompany.uframework.eventsourcing.store.model.TestEntityId
import io.github.ustudiocompany.uframework.eventsourcing.store.snapshot.query.SnapshotRow
import javax.sql.DataSource

internal class TestSnapshotStore(
    dataSource: DataSource,
    tableName: String,
    private val mapper: ObjectMapper,
    serializer: HistorySerializer<String>
) : SnapshotStore<TestAggregate, TestEntityId>(dataSource, serializer, tableName) {

    override fun createSnapshot(row: SnapshotRow): Result<TestAggregate, EventSourceRepositoryErrors.Snapshot.Deserialization> =
        try {
            val data = mapper.readValue(row.data, TestEntityData::class.java)
            val entity = TestEntity(
                id = TestEntityId(data.id),
                title = data.title,
                description = data.description
            )
            TestAggregate(revisions = row.revisions, entity = entity).success()
        } catch (expected: Exception) {
            EventSourceRepositoryErrors.Snapshot.Deserialization(expected).error()
        }

    override fun serializeData(
        aggregate: TestAggregate
    ): Result<String, EventSourceRepositoryErrors.Snapshot.Serialization> =
        try {
            val data = TestEntityData(
                id = aggregate.entity.id.asString(),
                title = aggregate.entity.title,
                description = aggregate.entity.description
            )
            mapper.writeValueAsString(data).success()
        } catch (expected: Exception) {
            EventSourceRepositoryErrors.Snapshot.Serialization(expected).error()
        }

    private class TestEntityData(
        @JsonProperty("id") val id: String,
        @JsonProperty("title") val title: String?,
        @JsonProperty("description") val description: String?
    )
}
