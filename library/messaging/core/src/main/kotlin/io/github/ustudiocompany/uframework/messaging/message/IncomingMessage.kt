package io.github.ustudiocompany.uframework.messaging.message

import io.github.ustudiocompany.uframework.messaging.message.header.Headers

public class IncomingMessage<out T>(
    public val routingKey: MessageRoutingKey,
    public val channel: Channel,
    public val headers: Headers,
    public val body: T?
) {

    public class Channel(
        public val name: String,
        public val partition: Int,
    )
}
