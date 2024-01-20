package io.github.ustudiocompany.uframework.saga.executor.result

import io.github.ustudiocompany.uframework.saga.publisher.Command
import io.github.ustudiocompany.uframework.saga.state.SagaExecutionState

internal class ExecutionResult<DATA>(
    val state: SagaExecutionState.Continuation<DATA>,
    val command: Command? = null,
    val hook: LifecycleHooks<DATA>? = null
)
