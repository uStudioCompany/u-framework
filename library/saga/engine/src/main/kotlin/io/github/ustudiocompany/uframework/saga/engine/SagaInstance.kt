package io.github.ustudiocompany.uframework.saga.engine

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.saga.core.Saga
import io.github.ustudiocompany.uframework.saga.core.SagaLabel
import io.github.ustudiocompany.uframework.saga.core.message.CommandMessage
import io.github.ustudiocompany.uframework.saga.core.message.ReplyMessage
import io.github.ustudiocompany.uframework.saga.engine.error.SagaErrors
import io.github.ustudiocompany.uframework.saga.engine.error.SagaExecutorErrors
import io.github.ustudiocompany.uframework.saga.engine.error.SagaPublisherErrors
import io.github.ustudiocompany.uframework.saga.engine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.saga.engine.executor.LifecycleHooks
import io.github.ustudiocompany.uframework.saga.engine.executor.execute
import io.github.ustudiocompany.uframework.saga.engine.publisher.Command
import io.github.ustudiocompany.uframework.saga.engine.publisher.CommandPublisher
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionState
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionStateRecord
import io.github.ustudiocompany.uframework.saga.engine.state.SerializedData
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext

@Suppress("TooManyFunctions")
public class SagaInstance<DATA>(
    private val saga: Saga<DATA>,
    private val publisher: CommandPublisher
) {

    public val label: SagaLabel
        get() = saga.definition.label

    context(Logging, DiagnosticContext)
    public fun startExecution(command: CommandMessage): ResultK<SagaExecutionStateRecord, SagaErrors> = resultWith {
        val (data) = initializeData(command)

        val state = SagaExecutionState.Initialization(
            routingKey = command.routingKey,
            correlationId = command.correlationId,
            messageId = command.messageId,
            data = data
        )

        val (result) = saga.execute(state)

        if (result.command != null) publish(command = result.command)

        //Calling lifecycle effect: onCompletedSuccessfully, onCompletedRollback
        result.hook?.run()

        result.toSagaExecutionStateRecord()
    }

    context(Logging, DiagnosticContext)
    public fun continueExecution(
        record: SagaExecutionStateRecord,
        reply: ReplyMessage
    ): ResultK<SagaExecutionStateRecord?, SagaErrors> = resultWith {
        val (data) = record.serializedData.deserialize()

        val state = SagaExecutionState.Continuation(
            routingKey = record.routingKey,
            correlationId = record.correlationId,
            messageId = record.messageId,
            direction = record.direction,
            status = record.status,
            history = record.history,
            data = data
        )

        val result = saga.execute(state, reply).bind() ?: return@resultWith Success.asNull

        if (result.command != null) publish(command = result.command)

        //Calling lifecycle effect: onCompletedSuccessfully, onCompletedRollback
        result.hook?.run()

        result.toSagaExecutionStateRecord()
    }

    context(Logging, DiagnosticContext)
    public fun stopExecution(
        record: SagaExecutionStateRecord
    ): ResultK<SagaExecutionStateRecord?, SagaErrors> = resultWith {
        val (data) = record.serializedData.deserialize()

        //Calling lifecycle effect: onCompletedSuccessfully, onCompletedRollback
        val hook = LifecycleHooks.OnStopped(correlationId = record.correlationId, data = data)
        hook.run()

        record.copy(status = SagaExecutionState.Status.STOP).asSuccess()
    }

    context(Logging, DiagnosticContext)
    public fun resumeExecution(
        record: SagaExecutionStateRecord
    ): ResultK<SagaExecutionStateRecord?, SagaErrors> = resultWith {
        val (data) = record.serializedData.deserialize()

        val state = SagaExecutionState.Resume(
            routingKey = record.routingKey,
            correlationId = record.correlationId,
            messageId = record.messageId,
            direction = record.direction,
            status = record.status,
            history = record.history,
            data = data
        )

        val (result) = saga.execute(state)

        if (result.command != null) publish(command = result.command)

        //Calling lifecycle effect: onCompletedSuccessfully, onCompletedRollback
        result.hook?.run()

        result.toSagaExecutionStateRecord()
    }

    private fun SerializedData.deserialize(): ResultK<DATA, SagaExecutorErrors.DataDeserialization> =
        saga.definition.serializer.deserialize(this.get)
            .mapFailure { SagaExecutorErrors.DataDeserialization(it) }

    private fun DATA.serialize(): ResultK<SerializedData, SagaExecutorErrors.DataSerialization> =
        saga.definition.serializer.serialize(this)
            .fold(
                onSuccess = { SerializedData(it).asSuccess() },
                onFailure = { SagaExecutorErrors.DataSerialization(it).asFailure() }
            )

    private fun initializeData(command: CommandMessage): ResultK<DATA, SagaExecutorErrors.DataInitialize> =
        saga.dataInitializer(command)
            .mapFailure { SagaExecutorErrors.DataInitialize(it) }

    context(Logging, DiagnosticContext)
    private fun publish(command: Command): ResultK<Unit, SagaPublisherErrors.CommandPublishing> =
        publisher.publish(command).mapFailure { SagaPublisherErrors.CommandPublishing(it) }

    private fun LifecycleHooks<DATA>.run() {
        when (this) {
            is LifecycleHooks.OnStarted -> saga.onStarted(correlationId)
            is LifecycleHooks.OnStopped -> saga.onStopped(correlationId, data)
            is LifecycleHooks.OnResumed -> saga.onResume(correlationId, data)
            is LifecycleHooks.OnCompletedSuccessfully -> saga.onCompletedSuccessfully(correlationId, data)
            is LifecycleHooks.OnCompletedRollback -> saga.onCompletedRollback(correlationId, data)
        }
    }

    private fun ExecutionResult<DATA>.toSagaExecutionStateRecord() =
        state.data.serialize()
            .map { serializedData ->
                SagaExecutionStateRecord(
                    routingKey = state.routingKey,
                    correlationId = state.correlationId,
                    messageId = state.messageId,
                    label = saga.definition.label,
                    direction = state.direction,
                    status = state.status,
                    history = state.history,
                    serializedData = serializedData
                )
            }
}
