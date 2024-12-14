package io.github.ustudiocompany.uframework.saga.engine.repository

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionStateRecord

public interface SagaRepository {
    public fun exists(correlationId: CorrelationId): ResultK<Boolean, Failure>
    public fun load(correlationId: CorrelationId): ResultK<SagaExecutionStateRecord?, Failure>
    public fun save(executionState: SagaExecutionStateRecord): ResultK<Boolean, Failure>
}
