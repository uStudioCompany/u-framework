package io.github.ustudiocompany.uframework.saga.core

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure

public interface SagaDataSerializer<DATA> {
    public fun serialize(data: DATA): ResultK<String?, Failure>
    public fun deserialize(value: String?): ResultK<DATA, Failure>
}
