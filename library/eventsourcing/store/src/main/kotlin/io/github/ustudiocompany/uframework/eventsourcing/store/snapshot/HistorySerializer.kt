package io.github.ustudiocompany.uframework.eventsourcing.store.snapshot

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.eventsourcing.aggregate.History
import io.github.ustudiocompany.uframework.failure.Failure

public interface HistorySerializer<T> {
    public fun serialize(history: History): T
    public fun deserialize(value: T): Result<History, Failure>
}
