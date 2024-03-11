package io.github.ustudiocompany.uframework.saga.engine.state

import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.saga.core.step.SagaStepLabel
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

public sealed interface HistoricalStep {
    public val index: Int
    public val label: SagaStepLabel
    public val messageId: MessageId

    /**
     * Крок в історії після якого є ще кроки.
     */
    public class Completed(
        override val index: Int,
        override val label: SagaStepLabel,
        override val messageId: MessageId
    ) : HistoricalStep

    /**
     * Останній крок в історії.
     */
    public class NotCompleted(
        override val index: Int,
        override val label: SagaStepLabel,
        override val messageId: MessageId
    ) : HistoricalStep
}

@OptIn(ExperimentalContracts::class)
public fun HistoricalStep.isCompleted(): Boolean {
    contract {
        returns(true) implies (this@isCompleted is HistoricalStep.Completed)
        returns(false) implies (this@isCompleted is HistoricalStep.NotCompleted)
    }
    return this is HistoricalStep.Completed
}
