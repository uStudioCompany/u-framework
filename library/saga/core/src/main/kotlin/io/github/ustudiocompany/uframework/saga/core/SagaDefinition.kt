package io.github.ustudiocompany.uframework.saga.core

import io.github.ustudiocompany.uframework.saga.core.step.SagaStep

public class SagaDefinition<DATA> internal constructor(
    public val label: SagaLabel,
    public val serializer: SagaDataSerializer<DATA>,
    public val steps: List<SagaStep<DATA>>
)
