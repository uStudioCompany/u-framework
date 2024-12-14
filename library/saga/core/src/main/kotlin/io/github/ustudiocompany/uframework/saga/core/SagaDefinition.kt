package io.github.ustudiocompany.uframework.saga.core

import io.github.airflux.commons.types.resultk.orThrow
import io.github.ustudiocompany.uframework.saga.core.step.SagaStep
import io.github.ustudiocompany.uframework.saga.core.step.SagaStepBuilder
import io.github.ustudiocompany.uframework.saga.core.step.SagaStepLabel

public fun <DATA> definition(
    label: String,
    builder: SagaDefinition.Builder<DATA>.() -> Unit
): SagaDefinition<DATA> =
    SagaDefinition.Builder<DATA>(SagaLabel.of(label)).apply(builder).build()

public class SagaDefinition<DATA> private constructor(
    public val label: SagaLabel,
    public val serializer: SagaDataSerializer<DATA>,
    public val steps: List<SagaStep<DATA>>
) {

    public class Builder<DATA> internal constructor(public val label: SagaLabel) {
        private val steps = mutableListOf<SagaStep<DATA>>()
        public var dataSerializer: SagaDataSerializer<DATA>? = null

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
            serializer = requireNotNull(dataSerializer) { "No serializer." },
            steps = steps
        )
    }
}
