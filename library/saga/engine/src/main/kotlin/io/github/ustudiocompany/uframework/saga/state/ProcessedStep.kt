package io.github.ustudiocompany.uframework.saga.state

import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.saga.step.StepLabel
import java.time.ZoneOffset
import java.time.ZonedDateTime

public class ProcessedStep private constructor(
    public val index: Int,
    public val label: StepLabel,
    public val messageId: MessageId,
    public val timestamp: ZonedDateTime
) {

    public companion object {
        public operator fun invoke(index: Int, label: StepLabel, messageId: MessageId): ProcessedStep =
            ProcessedStep(index, label, messageId, ZonedDateTime.now(ZoneOffset.UTC))
    }
}
