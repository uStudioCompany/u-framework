package io.github.ustudiocompany.uframework.messaging.publisher

import io.github.airflux.functional.onError
import io.github.ustudiocompany.uframework.messaging.handler.toMessageHandlerException
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.messaging.message.OutgoingMessage
import io.github.ustudiocompany.uframework.messaging.sender.MessageSender
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext
import kotlinx.coroutines.runBlocking
import java.util.*

public fun <T> deadLetterChannel(
    channelName: ChannelName,
    sender: MessageSender<T>
): DeadLetterChannel<T> =
    DeadLetterChannel(channelName, sender)

public class DeadLetterChannel<T>(private val channelName: ChannelName, private val sender: MessageSender<T>) {

    context(Logging, DiagnosticContext)
    public fun send(message: IncomingMessage<T>, stamp: Stamp) {
        val outgoingMessage = OutgoingMessage(
            headers = message.headers.add(Stamp.KEY to stamp.get),
            routingKey = message.routingKey,
            body = message.body
        )

        //TODO Fixed (runBlocking)
        runBlocking { sender.send(channelName, outgoingMessage) }
            .onError { failure -> throw failure.toMessageHandlerException() }
    }

    public class Stamp private constructor(public val get: String) {

        public companion object {

            @JvmStatic
            public fun <T> generate(message: IncomingMessage<T>): Stamp {
                val base = buildString { message.headers.map { item -> append(item.valueAsString()) } }
                return Stamp(UUID.nameUUIDFromBytes(base.toByteArray()).toString())
            }

            public const val KEY: String = "dead-letter-stamp"
        }
    }
}
