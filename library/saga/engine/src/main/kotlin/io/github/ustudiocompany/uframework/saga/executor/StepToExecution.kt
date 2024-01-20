package io.github.ustudiocompany.uframework.saga.executor

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.saga.request.Request
import io.github.ustudiocompany.uframework.saga.state.SagaExecutionState
import io.github.ustudiocompany.uframework.saga.step.StepLabel

public class StepToExecution<DATA>(
    public val position: Int,
    public val label: StepLabel,
    public val direction: SagaExecutionState.Direction,
    public val requestBuilder: (DATA) -> Result<Request, Failure>
)
