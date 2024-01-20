package io.github.ustudiocompany.uframework.saga.executor

import io.github.ustudiocompany.uframework.saga.state.SagaExecutionState
import io.github.ustudiocompany.uframework.saga.step.SagaStep
import io.github.ustudiocompany.uframework.saga.step.action.handler.ErrorReplyHandler
import io.github.ustudiocompany.uframework.saga.step.action.handler.SuccessfulReplyHandler
import io.github.ustudiocompany.uframework.saga.step.getErrorReplyHandler
import io.github.ustudiocompany.uframework.saga.step.getSuccessfulReplyHandler

public class CurrentExecutionStep<DATA>(
    public val position: Int,
    public val get: SagaStep<DATA>
)

public fun <DATA> CurrentExecutionStep<DATA>.getSuccessfulReplyHandler(
    direction: SagaExecutionState.Direction
): SuccessfulReplyHandler<DATA>? =
    get.getSuccessfulReplyHandler(direction)

public fun <DATA> CurrentExecutionStep<DATA>.getErrorReplyHandler(): ErrorReplyHandler<DATA>? =
    get.getErrorReplyHandler()
