@file:Suppress("TooManyFunctions")

package io.github.ustudiocompany.uframework.saga.engine.executor

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.ResultWith
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.map
import io.github.airflux.commons.types.result.mapFailure
import io.github.airflux.commons.types.result.success
import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.messaging.message.MessageRoutingKey
import io.github.ustudiocompany.uframework.saga.core.Saga
import io.github.ustudiocompany.uframework.saga.core.message.ReplyMessage
import io.github.ustudiocompany.uframework.saga.core.request.Request
import io.github.ustudiocompany.uframework.saga.core.request.RequestBuilder
import io.github.ustudiocompany.uframework.saga.core.step.SagaStepLabel
import io.github.ustudiocompany.uframework.saga.core.step.action.handler.ErrorReplyHandler
import io.github.ustudiocompany.uframework.saga.core.step.action.handler.ReplyHandlerErrors
import io.github.ustudiocompany.uframework.saga.core.step.action.handler.SuccessfulReplyHandler
import io.github.ustudiocompany.uframework.saga.engine.error.SagaExecutorErrors
import io.github.ustudiocompany.uframework.saga.engine.publisher.Command
import io.github.ustudiocompany.uframework.saga.engine.state.HistoricalStep
import io.github.ustudiocompany.uframework.saga.engine.state.ProcessedStep
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionState
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionState.Direction.COMPENSATION
import io.github.ustudiocompany.uframework.saga.engine.state.SagaExecutionState.Direction.PROCESSING
import io.github.ustudiocompany.uframework.saga.engine.state.StepHistory
import io.github.ustudiocompany.uframework.saga.engine.state.get
import io.github.ustudiocompany.uframework.saga.engine.state.isCompleted
import io.github.ustudiocompany.uframework.saga.engine.state.last
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext

context(Logging, DiagnosticContext)
internal fun <DATA> Saga<DATA>.execute(
    state: SagaExecutionState.Initialization<DATA>
): Result<ExecutionResult<DATA>, SagaExecutorErrors> =
    ResultWith {
        val firstStep = getFirstStep()

        //Request
        val (request) = firstStep.makeRequest(state.data)

        val direction = PROCESSING

        //Generate message-id
        val messageId: MessageId = generateMessageId(state.correlationId, firstStep.label, direction)

        return@ResultWith ExecutionResult(
            state = SagaExecutionState.Continuation<DATA>(
                routingKey = state.routingKey,
                correlationId = state.correlationId,
                messageId = state.messageId,
                direction = direction,
                status = SagaExecutionState.Status.ACTIVE,
                history = StepHistory(ProcessedStep(firstStep.position, firstStep.label, messageId)),
                data = state.data,
            ),
            command = request.toCommand(state.routingKey, state.correlationId, messageId),
            hook = LifecycleHooks.OnStarted(state.correlationId, state.data)
        ).success()
    }

/**
 * @return null як що повідомлення вже було оброблене
 */
context(Logging, DiagnosticContext)
internal fun <DATA> Saga<DATA>.execute(
    state: SagaExecutionState.Continuation<DATA>,
    reply: ReplyMessage
): Result<ExecutionResult<DATA>?, SagaExecutorErrors> =
    ResultWith {
        val saga = this@execute
        val historicalStep = state.findNotCompletedStep(reply.messageId).bind()
            ?: return@ResultWith Result.asNull
        val (currentExecutionStep) = getSagaStep(historicalStep)
        val (replyHandlingState) = currentExecutionStep.invokeReplyHandler(state.direction, reply, state.data)

        //Find the next step
        val (nextStep) = saga.getNextStep(replyHandlingState.direction, currentExecutionStep)
        if (nextStep == null) return@ResultWith sagaCompleted(state, replyHandlingState).success()

        //Request
        val command = nextStep.makeRequest(replyHandlingState.data).bind()
            .let { request ->
                //Generate message-id
                val messageId: MessageId = generateMessageId(state.correlationId, nextStep.label, nextStep.direction)
                request.toCommand(state.routingKey, state.correlationId, messageId)
            }

        return@ResultWith ExecutionResult(
            state = state.copy(
                direction = nextStep.direction,
                history = state.history + ProcessedStep(nextStep.position, nextStep.label, command.messageId),
                data = replyHandlingState.data
            ),
            command = command
        ).success()
    }

context(Logging, DiagnosticContext)
internal fun <DATA> Saga<DATA>.execute(
    state: SagaExecutionState.Resume<DATA>
): Result<ExecutionResult<DATA>, SagaExecutorErrors> =
    ResultWith {
        val (retryStep) = getRetryStep(state)

        val command = retryStep.makeRequest(state.data).bind()
            .toCommand(state.routingKey, state.correlationId, retryStep.messageId)

        return@ResultWith ExecutionResult(
            state = SagaExecutionState.Continuation<DATA>(
                routingKey = state.routingKey,
                correlationId = state.correlationId,
                messageId = state.messageId,
                direction = retryStep.direction,
                status = SagaExecutionState.Status.ACTIVE,
                history = state.history,
                data = state.data,
            ),
            command = command,
            hook = LifecycleHooks.OnResumed(state.correlationId, state.data)
        ).success()
    }

private fun <DATA> Saga<DATA>.getFirstStep(): StepToExecution<DATA> =
    definition.steps.first()
        .let { step ->
            StepToExecution(
                position = 0,
                label = step.label,
                direction = PROCESSING,
                requestBuilder = step.participant.requestBuilder
            )
        }

/**
 * @return `null` якщо повідомлення не потребує обробки.
 */
private fun <DATA> SagaExecutionState.Continuation<DATA>.findNotCompletedStep(
    messageId: MessageId
): Result<HistoricalStep.NotCompleted?, SagaExecutorErrors> = ResultWith {
    val (historicalStep) = getHistoricalStep(messageId)
    if (historicalStep.isCompleted()) return@ResultWith Result.asNull
    if (isActive)
        historicalStep.success()
    else
        SagaExecutorErrors.SagaIsNotActive.failure()
}

private fun <DATA> SagaExecutionState.Continuation<DATA>.getHistoricalStep(
    messageId: MessageId
): Result<HistoricalStep, SagaExecutorErrors> =
    history[messageId]?.success() ?: SagaExecutorErrors.ReplyNotRelevant.failure()

private val SagaExecutionState.Continuation<*>.isActive: Boolean
    get() = this.status == SagaExecutionState.Status.ACTIVE

private fun <DATA> Saga<DATA>.getSagaStep(
    historicalStep: HistoricalStep.NotCompleted
): Result<CurrentExecutionStep<DATA>, SagaExecutorErrors> =
    definition.steps
        .getOrNull(historicalStep.index)
        ?.let { sagaStep -> CurrentExecutionStep(position = historicalStep.index, get = sagaStep).success() }
        ?: SagaExecutorErrors.UnknownStep(historicalStep.index, historicalStep.label).failure()

private fun <DATA> CurrentExecutionStep<DATA>.invokeReplyHandler(
    direction: SagaExecutionState.Direction,
    reply: ReplyMessage,
    data: DATA
): Result<ReplyHandlingState<DATA>, SagaExecutorErrors> =
    when (direction) {
        PROCESSING -> when (reply) {
            is ReplyMessage.Success ->
                getSuccessfulReplyHandler(direction)
                    .invoke(reply, data)
                    .map { ReplyHandlingState(PROCESSING, data = it) }

            is ReplyMessage.Error ->
                getErrorReplyHandler()
                    .invoke(reply, data)
                    .map { ReplyHandlingState(COMPENSATION, data = it) }
        }

        COMPENSATION -> when (reply) {
            is ReplyMessage.Success ->
                getSuccessfulReplyHandler(direction)
                    .invoke(reply, data)
                    .map { ReplyHandlingState(COMPENSATION, data = it) }

            is ReplyMessage.Error -> SagaExecutorErrors.CompensationCommandError.failure()
        }
    }

private fun <DATA> SuccessfulReplyHandler<DATA>?.invoke(
    reply: ReplyMessage.Success,
    data: DATA
): Result<DATA, SagaExecutorErrors.Reply> =
    if (this != null)
        handle(data, reply).mapFailure {
            when (it) {
                is ReplyHandlerErrors.ReplyBodyMissing ->
                    SagaExecutorErrors.Reply.ReplyBodyMissing(it)

                is ReplyHandlerErrors.ReplyBodyDeserialization ->
                    SagaExecutorErrors.Reply.ReplyBodyDeserialization(it)

                is ReplyHandlerErrors.ReplyHandle -> SagaExecutorErrors.Reply.ReplyHandle(it)
            }
        }
    else
        data.success()

private fun <DATA> ErrorReplyHandler<DATA>?.invoke(
    reply: ReplyMessage.Error,
    data: DATA
): Result<DATA, SagaExecutorErrors.Reply> =
    if (this != null)
        handle(data, reply).mapFailure {
            when (it) {
                is ReplyHandlerErrors.ReplyBodyMissing ->
                    SagaExecutorErrors.Reply.ReplyBodyMissing(it)

                is ReplyHandlerErrors.ReplyBodyDeserialization ->
                    SagaExecutorErrors.Reply.ReplyBodyDeserialization(it)

                is ReplyHandlerErrors.ReplyHandle ->
                    SagaExecutorErrors.Reply.ReplyHandle(it)
            }
        }
    else
        data.success()

private fun <DATA> Saga<DATA>.getNextStep(
    direction: SagaExecutionState.Direction,
    currentExecutionStep: CurrentExecutionStep<DATA>
): Result<StepToExecution<DATA>?, SagaExecutorErrors> {
    val shift = if (direction == PROCESSING) 1 else -1
    var position = currentExecutionStep.position + shift
    val steps = definition.steps
    while (position >= 0 && position < steps.size) {
        val step = steps[position]
        when (direction) {
            PROCESSING ->
                return StepToExecution(
                    position = position,
                    label = step.label,
                    direction = direction,
                    requestBuilder = step.participant.requestBuilder
                ).success()

            COMPENSATION -> {
                val compensation = step.compensation
                if (compensation != null)
                    return StepToExecution(
                        position = position,
                        label = step.label,
                        direction = direction,
                        requestBuilder = compensation.requestBuilder
                    ).success()
            }
        }
        position += shift
    }
    return Result.asNull
}

private fun <DATA> sagaCompleted(
    state: SagaExecutionState.Continuation<DATA>,
    replyHandlingState: ReplyHandlingState<DATA>
): ExecutionResult<DATA> =
    when (replyHandlingState.direction) {
        PROCESSING -> ExecutionResult(
            state = state.copy(
                direction = replyHandlingState.direction,
                status = SagaExecutionState.Status.COMPLETED_SUCCESSFULLY,
                data = replyHandlingState.data
            ),
            hook = LifecycleHooks.OnCompletedSuccessfully(state.correlationId, replyHandlingState.data)
        )

        COMPENSATION -> ExecutionResult(
            state = state.copy(
                direction = replyHandlingState.direction,
                status = SagaExecutionState.Status.COMPLETED_ROLLBACK,
                data = replyHandlingState.data
            ),
            hook = LifecycleHooks.OnCompletedRollback(state.correlationId, replyHandlingState.data)
        )
    }

private fun <DATA> Saga<DATA>.getRetryStep(
    state: SagaExecutionState.Resume<DATA>
): Result<StepToRetryExecution<DATA>, SagaExecutorErrors> = ResultWith {
    val lastHistoricalStep = state.history.last()
    val (currentExecutionStep) = getSagaStep(lastHistoricalStep)
    StepToRetryExecution(
        position = currentExecutionStep.position,
        label = currentExecutionStep.get.label,
        direction = state.direction,
        requestBuilder = currentExecutionStep.get.participant.requestBuilder,
        messageId = lastHistoricalStep.messageId
    ).success()
}

private fun generateMessageId(
    correlationId: CorrelationId,
    label: SagaStepLabel,
    direction: SagaExecutionState.Direction
): MessageId =
    MessageId.generate(
        correlationId.get.lowercase() + ":" + label.get.lowercase() + ":" + direction.name.lowercase()
    )

private fun <DATA> StepToExecution<DATA>.makeRequest(data: DATA): Result<Request, SagaExecutorErrors.MakeRequest> =
    makeRequest(requestBuilder, data)

private fun <DATA> StepToRetryExecution<DATA>.makeRequest(data: DATA): Result<Request, SagaExecutorErrors.MakeRequest> =
    makeRequest(requestBuilder, data)

private fun <DATA> makeRequest(
    requestBuilder: RequestBuilder<DATA>,
    data: DATA
): Result<Request, SagaExecutorErrors.MakeRequest> =
    requestBuilder(data)
        .mapFailure { error -> SagaExecutorErrors.MakeRequest(error) }

private fun Request.toCommand(routingKey: MessageRoutingKey, correlationId: CorrelationId, messageId: MessageId) =
    Command(
        channel = channel,
        routingKey = routingKey,
        correlationId = correlationId,
        messageId = messageId,
        name = name,
        version = version,
        metadata = metadata,
        body = body
    )
