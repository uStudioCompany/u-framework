package io.github.ustudiocompany.uframework.saga.executor

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure

public fun interface SagaDataDeserializer<DATA> {
    public fun deserialize(value: String?): Result<DATA, Failure>
}
