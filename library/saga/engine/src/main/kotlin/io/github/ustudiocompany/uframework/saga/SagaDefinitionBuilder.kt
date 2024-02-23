package io.github.ustudiocompany.uframework.saga

import io.github.ustudiocompany.uframework.saga.executor.SagaDataSerializer
import io.github.ustudiocompany.uframework.saga.step.SagaStep
import io.github.ustudiocompany.uframework.saga.step.StepBuilder
import io.github.ustudiocompany.uframework.saga.step.StepLabel

public class SagaDefinitionBuilder<DATA> internal constructor(public val name: SagaLabel) {
    private val steps = mutableListOf<SagaStep<DATA>>()
    public var serializer: SagaDataSerializer<DATA>? = null

    public fun serializer(serializer: () -> SagaDataSerializer<DATA>): SagaDataSerializer<DATA> = serializer()

    public fun step(name: String, block: StepBuilder<DATA>.() -> Unit) {
        val stepName = StepLabel.of(name)
        val step = StepBuilder<DATA>(stepName).apply(block).build()
        addStep(step)
    }

    private fun addStep(step: SagaStep<DATA>) {
        steps.add(step)
    }

    internal fun build(): SagaDefinition<DATA> = SagaDefinition(
        name,
        serializer = requireNotNull(serializer) { "No serializer." },
        steps
    )
}
