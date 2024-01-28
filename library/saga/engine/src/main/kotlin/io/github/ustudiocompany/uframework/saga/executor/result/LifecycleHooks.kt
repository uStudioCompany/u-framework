package io.github.ustudiocompany.uframework.saga.executor.result

import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId

internal sealed interface LifecycleHooks<DATA> {

    data class OnStarted<DATA>(
        val correlationId: CorrelationId,
        val data: DATA
    ) : LifecycleHooks<DATA>

    data class OnCompletedSuccessfully<DATA>(
        val correlationId: CorrelationId,
        val data: DATA
    ) : LifecycleHooks<DATA>

    data class OnCompletedRollback<DATA>(
        val correlationId: CorrelationId,
        val data: DATA
    ) : LifecycleHooks<DATA>

    data class OnStopped<DATA>(
        val correlationId: CorrelationId,
        val data: DATA
    ) : LifecycleHooks<DATA>

    data class OnResumed<DATA>(
        val correlationId: CorrelationId,
        val data: DATA
    ) : LifecycleHooks<DATA>
}
