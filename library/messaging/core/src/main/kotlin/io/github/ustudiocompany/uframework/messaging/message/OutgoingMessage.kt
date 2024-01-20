package io.github.ustudiocompany.uframework.messaging.message

import io.github.ustudiocompany.uframework.messaging.message.header.Headers

public class OutgoingMessage<out T>(
    public val routingKey: MessageRoutingKey,
    public val headers: Headers,
    public val body: T?
)
