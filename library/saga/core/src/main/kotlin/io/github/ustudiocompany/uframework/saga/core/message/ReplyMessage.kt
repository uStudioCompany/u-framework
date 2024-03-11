package io.github.ustudiocompany.uframework.saga.core.message

import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.saga.core.MetaData

public sealed interface ReplyMessage {
    public val correlationId: CorrelationId
    public val messageId: MessageId

    public class Success(
        override val correlationId: CorrelationId,
        override val messageId: MessageId,
        public val metadata: MetaData = MetaData.Empty,
        public val body: String?
    ) : ReplyMessage

    public class Error(
        override val correlationId: CorrelationId,
        override val messageId: MessageId,
        public val metadata: MetaData = MetaData.Empty,
        public val body: String?
    ) : ReplyMessage
}
