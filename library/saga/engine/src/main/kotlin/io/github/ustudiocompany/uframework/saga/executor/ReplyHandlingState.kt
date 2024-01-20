package io.github.ustudiocompany.uframework.saga.executor

import io.github.ustudiocompany.uframework.saga.state.SagaExecutionState

public data class ReplyHandlingState<DATA>(
    public val direction: SagaExecutionState.Direction,
    public val data: DATA
)
