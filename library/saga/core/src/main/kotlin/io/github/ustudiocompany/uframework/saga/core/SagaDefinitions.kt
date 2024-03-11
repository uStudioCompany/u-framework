package io.github.ustudiocompany.uframework.saga.core

public fun saga(block: SagaDefinitions.Builder.() -> Unit): SagaDefinitions =
    SagaDefinitions.Builder().apply(block).build()

public class SagaDefinitions private constructor(
    private val definitions: Map<SagaLabel, SagaDefinition<*>>,
) {
    public fun findOrNull(name: SagaLabel): SagaDefinition<*>? = definitions[name]

    public class Builder internal constructor() {
        private val definitions = mutableMapOf<SagaLabel, SagaDefinition<*>>()

        public fun <DATA> register(sagaDefinition: SagaDefinition<DATA>) {
            if (sagaDefinition.label in definitions) error("Duplicate saga with the `${sagaDefinition.label}` name.")
            definitions[sagaDefinition.label] = sagaDefinition
        }

        internal fun build(): SagaDefinitions = SagaDefinitions(definitions)
    }
}
