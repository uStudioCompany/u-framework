package io.github.ustudiocompany.uframework.saga.step

import io.github.ustudiocompany.uframework.saga.state.SagaExecutionState
import io.github.ustudiocompany.uframework.saga.step.action.CompensationAction
import io.github.ustudiocompany.uframework.saga.step.action.InvokeParticipantAction
import io.github.ustudiocompany.uframework.saga.step.action.handler.ErrorReplyHandler
import io.github.ustudiocompany.uframework.saga.step.action.handler.SuccessfulReplyHandler

public class SagaStep<DATA>(
    public val label: SagaStepLabel,
    public val participant: InvokeParticipantAction<DATA>,
    public val compensation: CompensationAction<DATA>?
)

public fun <DATA> SagaStep<DATA>.getSuccessfulReplyHandler(
    direction: SagaExecutionState.Direction
): SuccessfulReplyHandler<DATA>? = when (direction) {
    SagaExecutionState.Direction.PROCESSING -> participant.successfulReplyHandler
    SagaExecutionState.Direction.COMPENSATION -> compensation?.successfulReplyHandler
}

public fun <DATA> SagaStep<DATA>.getErrorReplyHandler(): ErrorReplyHandler<DATA>? = participant.errorReplyHandler
