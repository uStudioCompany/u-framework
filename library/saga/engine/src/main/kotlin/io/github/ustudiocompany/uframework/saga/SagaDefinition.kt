package io.github.ustudiocompany.uframework.saga

import io.github.ustudiocompany.uframework.saga.executor.SagaDataSerializer
import io.github.ustudiocompany.uframework.saga.step.SagaStep

public class SagaDefinition<DATA> internal constructor(
    public val label: SagaLabel,
    public val serializer: SagaDataSerializer<DATA>,
    public val steps: List<SagaStep<DATA>>
)
