package io.github.ustudiocompany.uframework.saga.engine.executor

import io.github.ustudiocompany.uframework.saga.core.step.SagaStep
import io.github.ustudiocompany.uframework.saga.core.step.action.handler.ErrorReplyHandler
import io.github.ustudiocompany.uframework.saga.core.step.action.handler.SuccessfulReplyHandler
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionState

public class CurrentExecutionStep<DATA>(
    public val position: Int,
    public val get: SagaStep<DATA>
)

public fun <DATA> CurrentExecutionStep<DATA>.getSuccessfulReplyHandler(
    direction: SagaExecutionState.Direction
): SuccessfulReplyHandler<DATA>? {
    val step = get
    return when (direction) {
        SagaExecutionState.Direction.PROCESSING -> step.participant.successfulReplyHandler
        SagaExecutionState.Direction.COMPENSATION -> step.compensation?.successfulReplyHandler
    }
}

public fun <DATA> CurrentExecutionStep<DATA>.getErrorReplyHandler(): ErrorReplyHandler<DATA>? {
    val step = get
    return step.participant.errorReplyHandler
}
