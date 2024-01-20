package io.github.ustudiocompany.uframework.messaging.publisher

import io.github.airflux.functional.onError
import io.github.ustudiocompany.uframework.logging.api.Logging
import io.github.ustudiocompany.uframework.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.messaging.handler.toMessageHandlerException
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.messaging.message.OutgoingMessage
import io.github.ustudiocompany.uframework.messaging.sender.MessageSender
import kotlinx.coroutines.runBlocking
import java.util.*

public fun <T> deadLetterChannel(
    channelName: ChannelName,
    sender: MessageSender<T>
): DeadLetterChannel<T> =
    DeadLetterChannel(channelName, sender)

public class DeadLetterChannel<T>(private val channelName: ChannelName, private val sender: MessageSender<T>) {

    context(Logging, DiagnosticContext)
    @Deprecated("Use send with identifier")
    public fun send(message: IncomingMessage<T>) {
        val outgoingMessage = OutgoingMessage(
            headers = message.headers,
            routingKey = message.routingKey,
            body = message.body
        )

        //TODO Fixed (runBlocking)
        runBlocking { sender.send(channelName, outgoingMessage) }
            .onError { failure -> throw failure.toMessageHandlerException() }
    }

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

    @Deprecated("Use Identifier")
    public fun generateDiagnosticContextEntry(): DiagnosticContext.Entry =
        DiagnosticContext.Entry(
            key = DEAD_LETTER_ID_DIAGNOSTIC_CONTEXT_KEY,
            value = UUID.randomUUID().toString()
        )

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

    private companion object {
        private const val DEAD_LETTER_ID_DIAGNOSTIC_CONTEXT_KEY: String = "dead-letter-id"
    }
}
