package io.github.ustudiocompany.uframework.eventsourcing.store.snapshot

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Aggregate
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.failure.Failure

public interface SnapshotStore<AGGREGATE, ID>
    where AGGREGATE : Aggregate<ID>,
          ID : EntityId {

    public fun loadSnapshot(aggregateId: ID): Result<AGGREGATE?, Failure>
    public fun saveSnapshot(aggregate: AGGREGATE): Result<Boolean, Failure>
}
