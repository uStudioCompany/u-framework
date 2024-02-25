package io.github.ustudiocompany.uframework.saga.executor

import io.github.ustudiocompany.uframework.saga.request.RequestBuilder
import io.github.ustudiocompany.uframework.saga.state.SagaExecutionState
import io.github.ustudiocompany.uframework.saga.step.SagaStepLabel

public class StepToExecution<DATA>(
    public val position: Int,
    public val label: SagaStepLabel,
    public val direction: SagaExecutionState.Direction,
    public val requestBuilder: RequestBuilder<DATA>
)
