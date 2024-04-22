package io.github.ustudiocompany.uframework.saga.engine.repository

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionStateRecord

public interface SagaRepository {
    public fun exists(correlationId: CorrelationId): Result<Boolean, Failure>
    public fun load(correlationId: CorrelationId): Result<SagaExecutionStateRecord?, Failure>
    public fun save(executionState: SagaExecutionStateRecord): Result<Boolean, Failure>
}
