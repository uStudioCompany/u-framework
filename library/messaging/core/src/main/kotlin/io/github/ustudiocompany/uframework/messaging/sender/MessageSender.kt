package io.github.ustudiocompany.uframework.messaging.sender

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.messaging.message.OutgoingMessage
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext

public interface MessageSender<T> {

    context(Logging, DiagnosticContext)
    public suspend fun send(channelName: ChannelName, message: OutgoingMessage<T>): Result<SentMessageMetadata, Errors>

    public sealed class Errors : Failure {
        override val domain: String = "MESSAGE-SENDER"

        public class Send(channel: ChannelName, key: Any?, exception: Exception) : Errors() {
            override val number: String = "1"
            override val description: String =
                "The error of sending a message to channel `$channel` with key `$key`."
            override val cause: Failure.Cause = Failure.Cause.Exception(exception)
            override val details: Failure.Details = Failure.Details.of(
                MESSAGE_CHANNEL_NAME_DETAIL_KEY to channel.get,
                MESSAGE_KEY_DETAIL_KEY to (key?.toString() ?: "None")
            )

            private companion object {
                private const val MESSAGE_CHANNEL_NAME_DETAIL_KEY = "outbox-channel-name"
                private const val MESSAGE_KEY_DETAIL_KEY = "message-key"
            }
        }
    }
}
