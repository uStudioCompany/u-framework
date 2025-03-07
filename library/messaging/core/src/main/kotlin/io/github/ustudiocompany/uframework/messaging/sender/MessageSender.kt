package io.github.ustudiocompany.uframework.messaging.sender

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Details
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.messaging.message.OutgoingMessage
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext

public fun interface MessageSender<T> {

    context(Logging, DiagnosticContext)
    public suspend fun send(channelName: ChannelName, message: OutgoingMessage<T>): ResultK<SentMessageMetadata, Errors>

    public sealed class Errors : Failure {

        public class Send(channel: ChannelName, key: Any?, exception: Exception) : Errors() {
            override val code: String = PREFIX + "1"
            override val description: String =
                "The error of sending a message to channel `$channel` with key `$key`."
            override val cause: Failure.Cause = Failure.Cause.Exception(exception)
            override val details: Details = Details.of(
                MESSAGE_CHANNEL_NAME_DETAIL_KEY to channel.get,
                MESSAGE_KEY_DETAIL_KEY to (key?.toString() ?: "None")
            )

            private companion object {
                private const val PREFIX = "MESSAGE-SENDER-"
                private const val MESSAGE_CHANNEL_NAME_DETAIL_KEY = "outbox-channel-name"
                private const val MESSAGE_KEY_DETAIL_KEY = "message-key"
            }
        }
    }
}
