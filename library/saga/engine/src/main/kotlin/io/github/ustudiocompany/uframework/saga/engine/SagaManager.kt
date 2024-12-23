package io.github.ustudiocompany.uframework.saga.engine

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.saga.core.message.CommandMessage
import io.github.ustudiocompany.uframework.saga.core.message.ReplyMessage
import io.github.ustudiocompany.uframework.saga.engine.error.SagaErrors
import io.github.ustudiocompany.uframework.saga.engine.error.SagaManagerErrors
import io.github.ustudiocompany.uframework.saga.engine.error.SagaStorageErrors
import io.github.ustudiocompany.uframework.saga.engine.publisher.CommandPublisher
import io.github.ustudiocompany.uframework.saga.engine.repository.SagaRepository
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionStateRecord
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext

public class SagaManager(
    private val sagaResolver: SagaResolver,
    private val repository: SagaRepository,
    private val publisher: CommandPublisher
) {

    context(Logging, DiagnosticContext)
    public tailrec fun start(command: CommandMessage): ResultK<Unit, SagaErrors> = resultWith {
        val (isExist) = exists(command.correlationId)
        if (isExist) return@resultWith Success.asUnit
        val (saga) = command.getSaga()
        val sagaInstance = SagaInstance(saga, publisher)
        val (state) = sagaInstance.startExecution(command = command)
        val (isSaved) = save(state)
        return if (isSaved) Success.asUnit else start(command)
    }

    context(Logging, DiagnosticContext)
    public fun handle(reply: ReplyMessage): ResultK<Unit, SagaErrors> = resultWith {
        val (state) = load(reply.correlationId)
        val (saga) = state.getSaga()
        val sagaInstance = SagaInstance(saga, publisher)
        val newState = sagaInstance.continueExecution(record = state, reply = reply).bind() ?: return Success.asUnit
        val (isSaved) = save(newState)
        return if (isSaved) Success.asUnit else handle(reply)
    }

    context(Logging, DiagnosticContext)
    public tailrec fun stop(correlationId: CorrelationId): ResultK<Unit, SagaErrors> = resultWith {
        val (state) = load(correlationId)
        val (saga) = state.getSaga()
        val sagaInstance = SagaInstance(saga, publisher)
        val newState = sagaInstance.stopExecution(state).bind() ?: return Success.asUnit
        val (isSaved) = save(newState)
        return if (isSaved) Success.asUnit else stop(correlationId)
    }

    context(Logging, DiagnosticContext)
    public tailrec fun resume(correlationId: CorrelationId): ResultK<Unit, SagaErrors> = resultWith {
        val (state) = load(correlationId)
        val (saga) = state.getSaga()
        val sagaInstance = SagaInstance(saga, publisher)
        val newState = sagaInstance.resumeExecution(state).bind() ?: return Success.asUnit
        val (isSaved) = save(newState)
        return if (isSaved) Success.asUnit else resume(correlationId)
    }

    private fun CommandMessage.getSaga() =
        sagaResolver.resolve(name, version)
            ?.asSuccess()
            ?: SagaManagerErrors.SagaForCommandNotFound(correlationId, name, version).asFailure()

    private fun SagaExecutionStateRecord.getSaga() =
        sagaResolver.resolve(label)
            ?.asSuccess()
            ?: SagaManagerErrors.SagaForSagaInstanceNotFound(correlationId, label).asFailure()

    private fun exists(correlationId: CorrelationId): ResultK<Boolean, SagaStorageErrors.Storage> =
        repository.exists(correlationId).mapFailure { SagaStorageErrors.Storage(it) }

    private fun load(
        correlationId: CorrelationId
    ): ResultK<SagaExecutionStateRecord, SagaErrors> = resultWith {
        val (state) = repository.load(correlationId).mapFailure { SagaStorageErrors.Storage(it) }
        return state?.asSuccess() ?: SagaManagerErrors.SagaInstanceNotFound(correlationId).asFailure()
    }

    /**
     * Return false if a saga-instance is already.
     */
    private fun save(record: SagaExecutionStateRecord): ResultK<Boolean, SagaStorageErrors.Storage> =
        repository.save(record).mapFailure { SagaStorageErrors.Storage(it) }
}
