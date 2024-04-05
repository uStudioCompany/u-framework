package io.github.ustudiocompany.uframework.eventsourcing.command

import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId

public interface Command {
    public val correlationId: CorrelationId
    public val messageId: MessageId
}
