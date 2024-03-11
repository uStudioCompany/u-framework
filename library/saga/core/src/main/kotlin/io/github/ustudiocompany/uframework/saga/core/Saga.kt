package io.github.ustudiocompany.uframework.saga.core

import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId

public fun <DATA> saga(
    name: String,
    builder: SagaDefinitionBuilder<DATA>.() -> Unit
): SagaDefinition<DATA> =
    SagaDefinitionBuilder<DATA>(SagaLabel.of(name)).apply(builder).build()

public interface Saga<DATA> {
    public val definition: SagaDefinition<DATA>
    public val dataInitializer: SagaDataInitializer<DATA>

    public fun onStarted(correlationId: CorrelationId) {}

    public fun onCompletedSuccessfully(correlationId: CorrelationId, data: DATA) {}

    public fun onCompletedRollback(correlationId: CorrelationId, data: DATA) {}

    public fun onStopped(correlationId: CorrelationId, data: DATA) {}

    public fun onResume(correlationId: CorrelationId, data: DATA) {}
}
