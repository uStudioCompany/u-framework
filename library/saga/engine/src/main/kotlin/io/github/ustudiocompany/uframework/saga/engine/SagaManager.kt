package io.github.ustudiocompany.uframework.saga.engine

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.ResultWith
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.mapFailure
import io.github.airflux.commons.types.result.success
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
    public tailrec fun start(command: CommandMessage): Result<Unit, SagaErrors> = ResultWith {
        val (isExist) = exists(command.correlationId)
        if (isExist) return@ResultWith Result.asUnit
        val (saga) = command.getSaga()
        val sagaInstance = SagaInstance(saga, publisher)
        val (state) = sagaInstance.startExecution(command = command)
        val (isSaved) = save(state)
        return if (isSaved) Result.asUnit else start(command)
    }

    context(Logging, DiagnosticContext)
    public fun handle(reply: ReplyMessage): Result<Unit, SagaErrors> = ResultWith {
        val (state) = load(reply.correlationId)
        val (saga) = state.getSaga()
        val sagaInstance = SagaInstance(saga, publisher)
        val newState = sagaInstance.continueExecution(record = state, reply = reply).bind() ?: return Result.asUnit
        val (isSaved) = save(newState)
        return if (isSaved) Result.asUnit else handle(reply)
    }

    context(Logging, DiagnosticContext)
    public tailrec fun stop(correlationId: CorrelationId): Result<Unit, SagaErrors> = ResultWith {
        val (state) = load(correlationId)
        val (saga) = state.getSaga()
        val sagaInstance = SagaInstance(saga, publisher)
        val newState = sagaInstance.stopExecution(state).bind() ?: return Result.asUnit
        val (isSaved) = save(newState)
        return if (isSaved) Result.asUnit else stop(correlationId)
    }

    context(Logging, DiagnosticContext)
    public tailrec fun resume(correlationId: CorrelationId): Result<Unit, SagaErrors> = ResultWith {
        val (state) = load(correlationId)
        val (saga) = state.getSaga()
        val sagaInstance = SagaInstance(saga, publisher)
        val newState = sagaInstance.resumeExecution(state).bind() ?: return Result.asUnit
        val (isSaved) = save(newState)
        return if (isSaved) Result.asUnit else resume(correlationId)
    }

    private fun CommandMessage.getSaga() =
        sagaResolver.resolve(name, version)
            ?.success()
            ?: SagaManagerErrors.SagaForCommandNotFound(correlationId, name, version).failure()

    private fun SagaExecutionStateRecord.getSaga() =
        sagaResolver.resolve(label)
            ?.success()
            ?: SagaManagerErrors.SagaForSagaInstanceNotFound(correlationId, label).failure()

    private fun exists(correlationId: CorrelationId): Result<Boolean, SagaStorageErrors.Storage> =
        repository.exists(correlationId).mapFailure { SagaStorageErrors.Storage(it) }

    private fun load(
        correlationId: CorrelationId
    ): Result<SagaExecutionStateRecord, SagaErrors> = ResultWith {
        val (state) = repository.load(correlationId).mapFailure { SagaStorageErrors.Storage(it) }
        return state?.success() ?: SagaManagerErrors.SagaInstanceNotfound(correlationId).failure()
    }

    /**
     * Return false if a saga-instance is already.
     */
    private fun save(record: SagaExecutionStateRecord): Result<Boolean, SagaStorageErrors.Storage> =
        repository.save(record).mapFailure { SagaStorageErrors.Storage(it) }
}
