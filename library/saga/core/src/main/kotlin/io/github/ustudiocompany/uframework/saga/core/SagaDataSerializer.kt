package io.github.ustudiocompany.uframework.saga.core

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure

public interface SagaDataSerializer<DATA> {
    public fun serialize(data: DATA): Result<String?, Failure>
    public fun deserialize(value: String?): Result<DATA, Failure>
}
