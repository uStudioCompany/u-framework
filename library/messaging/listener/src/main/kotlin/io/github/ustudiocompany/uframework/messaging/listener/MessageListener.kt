package io.github.ustudiocompany.uframework.messaging.listener

import io.github.ustudiocompany.uframework.messaging.handler.MessageHandler
import io.github.ustudiocompany.uframework.messaging.handler.MessageHandlerException
import io.github.ustudiocompany.uframework.messaging.handler.toMessageHandlerException
import io.github.ustudiocompany.uframework.messaging.receiver.MessageReceiver
import io.github.ustudiocompany.uframework.messaging.receiver.MessageReceiverFactory
import io.github.ustudiocompany.uframework.telemetry.logging.api.Logging
import io.github.ustudiocompany.uframework.telemetry.logging.api.debug
import io.github.ustudiocompany.uframework.telemetry.logging.api.error
import io.github.ustudiocompany.uframework.telemetry.logging.api.info
import io.github.ustudiocompany.uframework.telemetry.logging.api.warn
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.entry
import io.github.ustudiocompany.uframework.telemetry.logging.diagnostic.context.withDiagnosticContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import kotlin.coroutines.CoroutineContext

context (Logging, DiagnosticContext)
public fun <T> messageListener(
    properties: MessageListener.Properties,
    receiverFactory: MessageReceiverFactory<T>,
    handler: MessageHandler<T>
): MessageListener<T> = MessageListener(properties, receiverFactory, handler)

public class MessageListener<T>(
    private val properties: Properties,
    private val receiverFactory: MessageReceiverFactory<T>,
    private val handler: MessageHandler<T>
) {

    public val name: String = listenerName(properties)

    context (Logging, DiagnosticContext)
    public fun run(coroutineContext: CoroutineContext? = null): Job = scope(coroutineContext, name)
        .launch {
            withShutdown { run() }
        }

    public class Properties(
        public val topics: List<String>,
        public val name: String? = null,
        public val delayBeforeRecreatingReceiver: Duration = Duration.ofSeconds(DELAY_OF_RECREATE_RECEIVER),
    ) {

        private companion object {
            private const val DELAY_OF_RECREATE_RECEIVER = 30L
        }
    }

    context(Shutdown, Logging, DiagnosticContext)
    private suspend fun run() {
        this@DiagnosticContext.withDiagnosticContext(entry(MESSAGES_LISTENER_NAME_DIAGNOSTIC_CONTEXT_KEY, name)) {
            logger.info { "Running message listener..." }

            var keepConsuming = true
            while (keepConsuming) {
                if (isShutdown) return
                keepConsuming =
                    withDiagnosticContext(
                        entry(LISTENER_MESSAGE_CHANNEL_NAME_DIAGNOSTIC_CONTEXT_KEY, properties.topics)
                    ) {
                        logger.info { "Creating message receiver..." }

                        receiverFactory.create()
                            .subscribeTo(properties.topics)
                            .receive(handler)
                    }

                if (keepConsuming) {
                    logger.warn { "Re-creating receiver..." }
                    delay(properties.delayBeforeRecreatingReceiver.toMillis())
                }
            }

            logger.info { "Message receiver closed." }
        }
    }

    private fun listenerName(properties: Properties): String {
        val name = properties.name ?: "message-listener"
        val id = UUID.randomUUID()
        return "$name | id:$id"
    }

    context (Logging, DiagnosticContext)
    private fun scope(coroutineContext: CoroutineContext? = null, listenerName: String): CoroutineScope {
        val coroutineName = CoroutineName(listenerName)
        return if (coroutineContext != null) {
            logger.debug { "Using passed coroutine context..." }
            CoroutineScope(coroutineContext + coroutineName)
        } else {
            logger.debug { "Creating the new coroutine context..." }
            CoroutineScope(coroutineName + dispatcher())
        }
    }

    context (Shutdown, Logging, DiagnosticContext)
    private fun <T> MessageReceiver<T>.subscribeTo(topics: List<String>): MessageReceiver<T> =
        apply { subscribe(topics) }
            .also {
                logger.info { "A receiver was subscribed to the channel(s): ${topics.joinToString()}." }
            }

    context (Shutdown, Logging, DiagnosticContext)
    private fun <T> MessageReceiver<T>.receive(handler: MessageHandler<T>): Boolean =
        use { receiver ->
            var needKeepConsuming: Boolean
            var needCommitSync = false
            try {
                needCommitSync = receiver.poll(handler)
                logger.info { "Receiving messages stopped." }
                needKeepConsuming = false
            } catch (expected: MessageHandlerException) {
                logger.error(
                    diagnosticContext = expected.diagnosticContext,
                    exception = expected.cause
                ) { "The message processing error. ${expected.description}" }
                needKeepConsuming = true
                needCommitSync = false
            } catch (expected: Exception) {
                logger.error(exception = expected) { "Unexpected error." }
                needKeepConsuming = true
                needCommitSync = false
            } finally {
                if (needCommitSync) tryCommitSync()
            }
            needKeepConsuming
        }

    context (Shutdown, Logging, DiagnosticContext)
    @Suppress("CognitiveComplexMethod")
    private fun <T> MessageReceiver<T>.poll(handler: MessageHandler<T>): Boolean {
        var needCommitSync = false
        logger.debug { "Starting to receive messages..." }

        while (true) {
            if (isShutdown) return needCommitSync

            val records = poll()
            if (!records.isEmpty) {
                val countMessages = records.count()

                withDiagnosticContext(entry(LISTENER_MESSAGES_COUNT_DIAGNOSTIC_CONTEXT_KEY, countMessages)) {
                    logger.debug { "Received a list of $countMessages message(s)." }

                    records.forEachIndexed { index, message ->
                        val messageIndex = index + 1
                        withDiagnosticContext(
                            entry(LISTENER_MESSAGE_ROUTING_KEY_DIAGNOSTIC_CONTEXT_KEY, message.routingKey) { it.get },
                            entry(LISTENER_MESSAGE_INDEX_DIAGNOSTIC_CONTEXT_KEY, messageIndex),
                            entry(LISTENER_MESSAGE_CHANNEL_PARTITION_DIAGNOSTIC_CONTEXT_KEY, message.channel.partition)
                        ) {
                            logger.debug {
                                "Handling message ($messageIndex/$countMessages) from channel " +
                                    "`${message.channel.name}[${message.channel.partition}]` " +
                                    "with key `${message.routingKey}`"
                            }

                            try {
                                handler.handle(message)
                            } catch (expected: MessageHandlerException) {
                                throw expected
                            } catch (expected: Exception) {
                                throw expected.toMessageHandlerException("Unexpected handling error.")
                            }
                        }
                    }

                    logger.debug { "Asynchronous commit of message(s)..." }

                    commitAsync()
                    needCommitSync = true
                }
            }
        }
    }

    context (Logging, DiagnosticContext)
    private fun <T> MessageReceiver<T>.tryCommitSync() =
        try {
            logger.info { "The attempt of the synchronous commit of message(s)..." }
            commitSync()
            logger.info { "The synchronous commit of message(s) is done." }
        } catch (ignored: Exception) {
            logger.error(exception = ignored) { "The error of the synchronous commit of message(s)" }
        }

    private class Dispatcher : ExecutorCoroutineDispatcher() {
        override val executor: ExecutorService = createThread()
        private val coroutineDispatcher = executor.asCoroutineDispatcher()

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            coroutineDispatcher.dispatch(context) { block.run() }
        }

        override fun close() {
            executor.shutdown()
        }

        companion object {
            private fun createThread(): ExecutorService =
                ScheduledThreadPoolExecutor(1) { runnable -> Thread(runnable) }
                    .apply {
                        removeOnCancelPolicy = true
                        maximumPoolSize = 1
                    }
        }
    }

    private companion object {
        private const val MESSAGES_LISTENER_NAME_DIAGNOSTIC_CONTEXT_KEY = "messages-listener-name"
        private const val LISTENER_MESSAGE_ROUTING_KEY_DIAGNOSTIC_CONTEXT_KEY = "listener-message-routing-key"
        private const val LISTENER_MESSAGE_CHANNEL_NAME_DIAGNOSTIC_CONTEXT_KEY = "listener-message-channel-name"
        private const val LISTENER_MESSAGE_CHANNEL_PARTITION_DIAGNOSTIC_CONTEXT_KEY =
            "listener-message-channel-partition"
        private const val LISTENER_MESSAGES_COUNT_DIAGNOSTIC_CONTEXT_KEY = "listener-message-count"
        private const val LISTENER_MESSAGE_INDEX_DIAGNOSTIC_CONTEXT_KEY = "listener-message-index"

        private fun dispatcher(): CoroutineDispatcher = Dispatcher()
    }
}
