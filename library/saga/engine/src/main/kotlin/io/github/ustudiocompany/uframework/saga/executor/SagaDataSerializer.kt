package io.github.ustudiocompany.uframework.saga.executor

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure

public fun interface SagaDataSerializer<DATA> {
    public fun serialize(data: DATA): Result<String?, Failure>
}
