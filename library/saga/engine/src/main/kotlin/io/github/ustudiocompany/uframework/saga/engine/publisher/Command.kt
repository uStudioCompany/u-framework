package io.github.ustudiocompany.uframework.saga.engine.publisher

import io.github.ustudiocompany.uframework.messaging.header.type.CorrelationId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.messaging.message.MessageRoutingKey
import io.github.ustudiocompany.uframework.saga.core.MetaData

public data class Command(
    public val channel: ChannelName,
    public val routingKey: MessageRoutingKey,
    public val correlationId: CorrelationId,
    public val messageId: MessageId,
    public val name: MessageName,
    public val version: MessageVersion,
    public val metadata: MetaData,
    public val body: String?
)
