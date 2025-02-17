package io.github.ustudiocompany.uframework.saga.engine.state

import io.github.airflux.commons.collections.list.NonEmptyList
import io.github.airflux.commons.collections.list.nonEmptyListOf
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

@JvmInline
public value class StepHistory(public val get: NonEmptyList<ProcessedStep>) {

    public constructor(processedStep: ProcessedStep) : this(nonEmptyListOf(processedStep))

    public operator fun plus(processedStep: ProcessedStep): StepHistory =
        StepHistory(NonEmptyList.plus(get, processedStep))

    public operator fun contains(messageId: MessageId): Boolean =
        get.any { it.messageId == messageId }
}

public operator fun StepHistory.get(messageId: MessageId): HistoricalStep? {
    val list: List<ProcessedStep> = get.toList()
    return list.indexOf(messageId)
        ?.let { index ->
            val item = list[index]
            if (index isLastIn list)
                HistoricalStep.NotCompleted(index = item.index, label = item.label, messageId = item.messageId)
            else
                HistoricalStep.Completed(index = item.index, label = item.label, messageId = item.messageId)
        }
}

public fun StepHistory.last(): HistoricalStep.NotCompleted {
    val last = get.toList().last()
    return HistoricalStep.NotCompleted(index = last.index, label = last.label, messageId = last.messageId)
}

private fun List<ProcessedStep>.indexOf(messageId: MessageId) =
    indexOfLast { it.messageId == messageId }
        .takeIf { index -> index != -1 }

private infix fun Int.isLastIn(list: List<ProcessedStep>) = this == list.lastIndex
