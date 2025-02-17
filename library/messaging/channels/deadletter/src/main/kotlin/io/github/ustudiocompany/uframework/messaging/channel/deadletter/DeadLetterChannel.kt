package io.github.ustudiocompany.uframework.messaging.channel.deadletter

import io.github.airflux.commons.types.resultk.onFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.exceptionOrNull
import io.github.ustudiocompany.uframework.failure.fullDescription
import io.github.ustudiocompany.uframework.messaging.handler.toMessageHandlerException
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.messaging.message.OutgoingMessage
import io.github.ustudiocompany.uframework.messaging.sender.MessageSender
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.api.error
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.entry
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.withDiagnosticContext
import kotlinx.coroutines.runBlocking
import java.util.*

public fun <T> deadLetterChannel(
    channelName: ChannelName,
    sender: MessageSender<T>
): DeadLetterChannel<T> =
    DeadLetterChannel(channelName, sender)

public class DeadLetterChannel<T>(public val name: ChannelName, private val sender: MessageSender<T>) {

    context(Logging, DiagnosticContext)
    public fun send(message: IncomingMessage<T>, stamp: Stamp) {
        val outgoingMessage = OutgoingMessage(
            headers = message.headers.add(STAMP_KEY to stamp.get),
            routingKey = message.routingKey,
            body = message.body
        )

        //TODO Fixed (runBlocking)
        runBlocking { sender.send(name, outgoingMessage) }
            .onFailure { failure -> throw failure.toMessageHandlerException() }
    }

    public class Stamp private constructor(public val get: String) {

        public companion object {

            @JvmStatic
            public fun <T> generate(message: IncomingMessage<T>): Stamp {
                val base = buildString { message.headers.map { item -> append(item.valueAsString()) } }
                return Stamp(UUID.nameUUIDFromBytes(base.toByteArray()).toString())
            }
        }
    }

    public companion object {
        public const val STAMP_KEY: String = "dead-letter-stamp"
        public const val CHANNEL_NAME_KEY: String = "dead-letter-channel-name"
    }
}

context(Logging, DiagnosticContext)
public fun <T> IncomingMessage<T>.sendToDeadLetterChannel(
    channel: DeadLetterChannel<T>,
    description: String? = null,
    cause: Failure
) {
    val stamp = DeadLetterChannel.Stamp.generate(this)
    withDiagnosticContext(
        cause,
        entry(DeadLetterChannel.CHANNEL_NAME_KEY, channel.name),
        entry(DeadLetterChannel.STAMP_KEY, stamp.get)
    ) {
        logger.error(cause.exceptionOrNull()) {
            if (description != null)
                "$description ${cause.fullDescription()}"
            else
                cause.fullDescription()
        }
    }
    channel.send(this, stamp)
}
