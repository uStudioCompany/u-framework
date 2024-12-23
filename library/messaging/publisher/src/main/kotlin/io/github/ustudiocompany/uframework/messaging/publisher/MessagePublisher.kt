package io.github.ustudiocompany.uframework.messaging.publisher

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.messaging.message.OutgoingMessage
import io.github.ustudiocompany.uframework.messaging.sender.MessageSender
import io.github.ustudiocompany.uframework.messaging.sender.SentMessageMetadata
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.api.debug
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.entry
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.withDiagnosticContext
import kotlinx.coroutines.runBlocking

public fun <T : Any> publisher(channelName: ChannelName, sender: MessageSender<T>): MessagePublisher.Fixed<T> =
    MessagePublisher.Fixed(channelName, sender)

public fun <T : Any> publisher(sender: MessageSender<T>): MessagePublisher.Dynamic<T> =
    MessagePublisher.Dynamic(sender)

public sealed class MessagePublisher<T : Any>(private val sender: MessageSender<T>) {

    public class Fixed<T : Any>(
        public val channelName: ChannelName,
        sender: MessageSender<T>
    ) : MessagePublisher<T>(sender) {

        context(Logging, DiagnosticContext)
        public fun publish(message: OutgoingMessage<T>): ResultK<SentMessageMetadata, Errors> =
            tryPublish(channelName, message)
    }

    public class Dynamic<T : Any>(sender: MessageSender<T>) : MessagePublisher<T>(sender) {

        context(Logging, DiagnosticContext)
        public fun publish(
            channelName: ChannelName,
            message: OutgoingMessage<T>
        ): ResultK<SentMessageMetadata, Errors> = tryPublish(channelName, message)
    }

    public sealed class Errors : Failure {

        public class Publish(failure: MessageSender.Errors) : Errors() {
            override val code: String = PREFIX + "1"
            override val description: String = "A message publishing error."
            override val details: Failure.Details = Failure.Details.NONE
            override val cause: Failure.Cause = Failure.Cause.Failure(failure)
        }

        private companion object {
            private const val PREFIX = "MESSAGE-PUBLISHER-"
        }
    }

    context(Logging, DiagnosticContext)
    protected fun tryPublish(
        channelName: ChannelName,
        message: OutgoingMessage<T>
    ): ResultK<SentMessageMetadata, Errors> {
        withDiagnosticContext(
            entry(CHANNEL_NAME_DIAGNOSTIC_CONTEXT_KEY, channelName.get),
            entry(MESSAGE_ROUTING_KEY_DIAGNOSTIC_CONTEXT_KEY, message.routingKey) { it.get }
        ) {
            logger.debug { "Publishing message..." }
            //TODO Fixed (runBlocking)
            return runBlocking { sender.send(channelName, message) }
                .mapFailure { failure -> Errors.Publish(failure) }
        }
    }

    private companion object {
        private const val CHANNEL_NAME_DIAGNOSTIC_CONTEXT_KEY = "publisher-channel-name"
        private const val MESSAGE_ROUTING_KEY_DIAGNOSTIC_CONTEXT_KEY = "publisher-message-routing-key"
    }
}
