package io.github.ustudiocompany.uframework.saga.repository

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.message.header.type.CorrelationId
import io.github.ustudiocompany.uframework.saga.state.SagaExecutionStateRecord

public interface SagaRepository {
    public fun exists(correlationId: CorrelationId): Result<Boolean, Failure>
    public fun load(correlationId: CorrelationId): Result<SagaExecutionStateRecord?, Failure>
    public fun save(executionState: SagaExecutionStateRecord): Result<Boolean, Failure>
}
