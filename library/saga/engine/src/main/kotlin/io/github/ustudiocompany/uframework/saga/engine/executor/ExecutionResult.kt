package io.github.ustudiocompany.uframework.saga.engine.executor

import io.github.ustudiocompany.uframework.saga.engine.publisher.Command
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionState

internal class ExecutionResult<DATA>(
    val state: SagaExecutionState.Continuation<DATA>,
    val command: Command? = null,
    val hook: LifecycleHooks<DATA>? = null
)
