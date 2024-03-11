package io.github.ustudiocompany.uframework.saga.engine.executor

import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionState

public data class ReplyHandlingState<DATA>(
    public val direction: SagaExecutionState.Direction,
    public val data: DATA
)
