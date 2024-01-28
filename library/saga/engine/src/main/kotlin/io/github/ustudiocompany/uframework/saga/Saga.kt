package io.github.ustudiocompany.uframework.saga

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.saga.message.CommandMessage

public fun <DATA> saga(
    name: String,
    builder: SagaDefinitionBuilder<DATA>.() -> Unit
): SagaDefinition<DATA> =
    SagaDefinitionBuilder<DATA>(SagaLabel.of(name)).apply(builder).build()

public interface Saga<DATA> {
    public val definition: SagaDefinition<DATA>
    public val dataInitializer: (CommandMessage) -> Result<DATA, Failure>

    public fun onStarted(correlationId: CorrelationId) {}

    public fun onCompletedSuccessfully(correlationId: CorrelationId, data: DATA) {}

    public fun onCompletedRollback(correlationId: CorrelationId, data: DATA) {}

    public fun onStopped(correlationId: CorrelationId, data: DATA) {}

    public fun onResume(correlationId: CorrelationId, data: DATA) {}
}
