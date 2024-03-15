package io.github.ustudiocompany.uframework.saga.engine.state

import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.saga.core.step.SagaStepLabel
import java.time.ZoneOffset
import java.time.ZonedDateTime

public data class ProcessedStep(
    public val index: Int,
    public val label: SagaStepLabel,
    public val messageId: MessageId,
    public val timestamp: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)
)
