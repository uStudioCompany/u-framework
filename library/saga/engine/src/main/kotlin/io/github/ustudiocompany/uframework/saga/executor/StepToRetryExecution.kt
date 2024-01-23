package io.github.ustudiocompany.uframework.saga.executor

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.saga.request.Request
import io.github.ustudiocompany.uframework.saga.state.SagaExecutionState
import io.github.ustudiocompany.uframework.saga.step.StepLabel

public class StepToRetryExecution<DATA>(
    public val position: Int,
    public val label: StepLabel,
    public val direction: SagaExecutionState.Direction,
    public val requestBuilder: (DATA) -> Result<Request, Failure>,
    public val messageId: MessageId
)
