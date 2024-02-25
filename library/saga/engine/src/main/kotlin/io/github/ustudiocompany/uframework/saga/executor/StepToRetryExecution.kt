package io.github.ustudiocompany.uframework.saga.executor

import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.saga.request.RequestBuilder
import io.github.ustudiocompany.uframework.saga.state.SagaExecutionState
import io.github.ustudiocompany.uframework.saga.step.StepLabel

public class StepToRetryExecution<DATA>(
    public val position: Int,
    public val label: StepLabel,
    public val direction: SagaExecutionState.Direction,
    public val requestBuilder: RequestBuilder<DATA>,
    public val messageId: MessageId
)
