package io.github.ustudiocompany.uframework.saga.core

import io.github.airflux.functional.orThrow
import io.github.ustudiocompany.uframework.saga.core.step.SagaStep
import io.github.ustudiocompany.uframework.saga.core.step.SagaStepBuilder
import io.github.ustudiocompany.uframework.saga.core.step.SagaStepLabel

public class SagaDefinitionBuilder<DATA> internal constructor(public val label: SagaLabel) {
    private val steps = mutableListOf<SagaStep<DATA>>()
    public var serializer: SagaDataSerializer<DATA>? = null

    public fun serializer(serializer: () -> SagaDataSerializer<DATA>): SagaDataSerializer<DATA> = serializer()

    public fun step(label: String, block: SagaStepBuilder<DATA>.() -> Unit) {
        val sagaStepLabel = SagaStepLabel.of(label).orThrow { IllegalArgumentException(it.description) }
        val step = SagaStepBuilder<DATA>(sagaStepLabel).apply(block).build()
        addStep(step)
    }

    private fun addStep(step: SagaStep<DATA>) {
        steps.add(step)
    }

    internal fun build(): SagaDefinition<DATA> = SagaDefinition(
        label = label,
        serializer = requireNotNull(serializer) { "No serializer." },
        steps = steps
    )
}
