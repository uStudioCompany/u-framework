package com.dream.umbrella.lib.eventsourcing.store.snapshot

import com.dream.umbrella.lib.eventsourcing.aggregate.History
import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure

public interface HistorySerializer<T> {
    public fun serialize(history: History): T
    public fun deserialize(value: T): Result<History, Failure>
}
