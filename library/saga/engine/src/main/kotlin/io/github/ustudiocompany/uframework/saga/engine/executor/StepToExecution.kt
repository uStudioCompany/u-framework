package io.github.ustudiocompany.uframework.saga.engine.executor

import io.github.ustudiocompany.uframework.saga.core.request.RequestBuilder
import io.github.ustudiocompany.uframework.saga.core.step.SagaStepLabel
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionState

public class StepToExecution<DATA>(
    public val position: Int,
    public val label: SagaStepLabel,
    public val direction: SagaExecutionState.Direction,
    public val requestBuilder: RequestBuilder<DATA>
)
