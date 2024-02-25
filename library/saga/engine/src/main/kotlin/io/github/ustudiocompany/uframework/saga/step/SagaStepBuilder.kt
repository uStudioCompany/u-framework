package io.github.ustudiocompany.uframework.saga.step

import io.github.ustudiocompany.uframework.saga.request.RequestBuilder
import io.github.ustudiocompany.uframework.saga.step.action.CompensationAction
import io.github.ustudiocompany.uframework.saga.step.action.InvokeParticipantAction

public class SagaStepBuilder<DATA> internal constructor(private val label: StepLabel) {
    private var invokeParticipantActionBuilder: InvokeParticipantAction.Builder<DATA>? = null
    private var compensationActionBuilder: CompensationAction.Builder<DATA>? = null

    public fun invokeParticipant(requestBuilder: RequestBuilder<DATA>) {
        if (invokeParticipantActionBuilder != null) error("Re-definition the action.")
        invokeParticipantActionBuilder = InvokeParticipantAction.Builder(requestBuilder)
    }

    public fun invokeParticipant(
        requestBuilder: RequestBuilder<DATA>,
        block: InvokeParticipantAction.Builder<DATA>.() -> Unit
    ) {
        if (invokeParticipantActionBuilder != null) error("Re-definition the action.")
        invokeParticipantActionBuilder = InvokeParticipantAction.Builder(requestBuilder).apply(block)
    }

    public fun compensation(
        requestBuilder: RequestBuilder<DATA>,
        block: CompensationAction.Builder<DATA>.() -> Unit
    ) {
        if (invokeParticipantActionBuilder != null) error("Re-definition the compensation action.")
        compensationActionBuilder = CompensationAction.Builder(requestBuilder).apply(block)
    }

    public fun compensation(requestBuilder: RequestBuilder<DATA>) {
        if (invokeParticipantActionBuilder != null) error("Re-definition the compensation action.")
        compensationActionBuilder = CompensationAction.Builder(requestBuilder)
    }

    internal fun build(): SagaStep<DATA> =
        SagaStep(
            label = label,
            participant = requireNotNull(invokeParticipantActionBuilder) { "The action is not specified." }
                .build(),
            compensation = compensationActionBuilder?.build()
        )
}
