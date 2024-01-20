package io.github.ustudiocompany.uframework.messaging.receiver

import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.messaging.message.MessageRoutingKey
import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import org.apache.kafka.clients.consumer.ConsumerRecord

public fun <T> ConsumerRecord<String?, T>.toInboxMessage(): IncomingMessage<T> =
    IncomingMessage<T>(
        channel = IncomingMessage.Channel(name = topic(), partition = partition()),
        routingKey = MessageRoutingKey.of(key()),
        body = value(),
        headers = headers()
            .map { header -> Header(name = header.key(), value = header.value()) }
            .let { Headers(it) }
    )
