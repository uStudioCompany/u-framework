package io.github.ustudiocompany.uframework.saga.core.step

import io.github.ustudiocompany.uframework.saga.core.step.action.CompensationAction
import io.github.ustudiocompany.uframework.saga.core.step.action.InvokeParticipantAction

public class SagaStep<DATA>(
    public val label: SagaStepLabel,
    public val participant: InvokeParticipantAction<DATA>,
    public val compensation: CompensationAction<DATA>?
)
