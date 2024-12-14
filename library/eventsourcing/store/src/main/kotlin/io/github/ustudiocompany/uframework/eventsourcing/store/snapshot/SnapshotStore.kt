package io.github.ustudiocompany.uframework.eventsourcing.store.snapshot

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.Aggregate
import io.github.ustudiocompany.uframework.eventsourcing.entity.EntityId
import io.github.ustudiocompany.uframework.failure.Failure

public interface SnapshotStore<AGGREGATE, ID>
    where AGGREGATE : Aggregate<ID>,
          ID : EntityId {

    public fun loadSnapshot(aggregateId: ID): ResultK<AGGREGATE?, Failure>
    public fun saveSnapshot(aggregate: AGGREGATE): ResultK<Boolean, Failure>
}
