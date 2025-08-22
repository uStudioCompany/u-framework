package io.github.ustudiocompany.uframework.messaging.listener

import io.github.ustudiocompany.uframework.messaging.handler.MessageHandler
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessages
import io.github.ustudiocompany.uframework.messaging.message.MessageRoutingKey
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import io.github.ustudiocompany.uframework.messaging.receiver.MessageReceiver
import io.github.ustudiocompany.uframework.messaging.receiver.MessageReceiverFactory
import io.github.ustudiocompany.uframework.test.kotest.ComponentTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.Duration

internal class MessageListenerTest : ComponentTest() {

    init {

        "Test receiver" - {

            "when no messages for handling" - {
                val receiver: MessageReceiver<String> = mock {
                    on { poll() }.doReturn(IncomingMessages.Empty)
                }

                val factory: MessageReceiverFactory<String> = mock {
                    on { create() } doReturn receiver
                }

                val messageHandler: MessageHandler<String> = mock()

                val workerProps = MessageListener.Properties(
                    topics = listOf(FIRST_TOPIC, SECOND_TOPIC),
                    name = "message-worker",
                    delayBeforeRecreatingReceiver = Duration.ofMillis(300)
                )

                runBlocking {
                    val worker = messageListener(workerProps, factory, messageHandler).run()
                    delay(1000)
                    worker.cancel()
                }

                "then the receiver creates only one time" {
                    verify(factory, times(1)).create()
                }

                "then the receiver subscribes only once time" {
                    verify(receiver, times(1)).subscribe(listOf(FIRST_TOPIC, SECOND_TOPIC))
                }

                "then the poll method calls at least 2 time" {
                    verify(receiver, atLeast(2)).poll()
                }

                "then asynchronous commit is never called" {
                    verify(receiver, times(0)).commitAsync()
                }

                "then synchronous commit is never called" {
                    verify(receiver, times(0)).commitSync()
                }

                "then no messages were handled" {
                    verify(messageHandler, times(0)).apply {
                        handle(any<IncomingMessage<String>>())
                    }
                }
            }

            "when only one message package was received with two messages" - {
                val receiver: MessageReceiver<String> = mock {
                    on { poll() }.doReturn(messages(FIRST_MESSAGE, SECOND_MESSAGE), IncomingMessages.Empty)
                }

                val factory: MessageReceiverFactory<String> = mock {
                    on { create() } doReturn receiver
                }

                val messageHandler: MessageHandler<String> = mock()

                val workerProps = MessageListener.Properties(
                    topics = listOf(FIRST_TOPIC, SECOND_TOPIC),
                    name = "message-worker",
                    delayBeforeRecreatingReceiver = Duration.ofMillis(300)
                )

                runBlocking {
                    val worker = messageListener(workerProps, factory, messageHandler).run()
                    delay(1000)
                    worker.cancel()
                }

                "then the receiver creates only one time" {
                    verify(factory, times(1)).create()
                }

                "then the receiver subscribes only once time" {
                    verify(receiver, times(1)).subscribe(listOf(FIRST_TOPIC, SECOND_TOPIC))
                }

                "then messages were requested at least 2 time" {
                    verify(receiver, atLeast(2)).poll()
                }

                "then asynchronous commit is called only one time" {
                    verify(receiver, times(1)).commitAsync()
                }

                "then synchronous commit is called only one times" {
                    verify(receiver, times(1)).commitSync()
                }

                "then all messages from the package were handled" {
                    verify(messageHandler, times(2)).apply {
                        handle(any<IncomingMessage<String>>())
                    }
                }
            }

            "when some error occurred while handling some message" - {
                val receiver: MessageReceiver<String> = mock {
                    on { poll() } doReturn messages(FIRST_MESSAGE)
                }

                val factory: MessageReceiverFactory<String> = mock {
                    on { create() } doReturn receiver
                }

                val messageHandler: MessageHandler<String> = mock {
                    on {
                        handle(any<IncomingMessage<String>>())
                    }.thenThrow(IllegalArgumentException("Some error."))
                }

                val workerProps = MessageListener.Properties(
                    topics = listOf(FIRST_TOPIC, SECOND_TOPIC),
                    name = "message-worker",
                    delayBeforeRecreatingReceiver = Duration.ofMillis(200)
                )

                runBlocking {
                    messageListener(workerProps, factory, messageHandler).run()
                    delay(1000)
                }

                "then the receiver was created after each error" {
                    verify(factory, atLeast(2)).create()
                }

                "then the receiver was subscribed after each creation" {
                    verify(receiver, atLeast(2)).subscribe(listOf(FIRST_TOPIC, SECOND_TOPIC))
                }

                "then messages were requested after each creation of the receiver" {
                    verify(receiver, atLeast(2)).poll()
                }

                "then asynchronous commit is never called" {
                    verify(receiver, times(0)).commitAsync()
                }

                "then synchronous commit is never called" {
                    verify(receiver, times(0)).commitSync()
                }

                "then an attempt was made to handle messages" {
                    verify(messageHandler, atLeast(2)).apply {
                        handle(any<IncomingMessage<String>>())
                    }
                }
            }

            "when some error occurred in the receiver after requesting messages" - {
                val receiver: MessageReceiver<String> = mock {
                    on { poll() } doThrow IllegalArgumentException()
                }

                val factory: MessageReceiverFactory<String> = mock {
                    on { create() } doReturn receiver
                }

                val messageHandler: MessageHandler<String> = mock()

                val workerProps = MessageListener.Properties(
                    topics = listOf(FIRST_TOPIC),
                    name = "message-worker",
                    delayBeforeRecreatingReceiver = Duration.ofMillis(300)
                )

                runBlocking {
                    val worker = messageListener(workerProps, factory, messageHandler).run()
                    delay(1000)
                    worker.cancel()
                }

                "then the receiver was created after each error" {
                    verify(factory, atLeast(2)).create()
                }

                "then the receiver was subscribed after each creation" {
                    verify(receiver, atLeast(2)).subscribe(listOf(FIRST_TOPIC))
                }

                "then messages were requested after each creation of the receiver" {
                    verify(receiver, atLeast(2)).poll()
                }

                "then asynchronous commit is never called" {
                    verify(receiver, times(0)).commitAsync()
                }

                "then synchronous commit is never called" {
                    verify(receiver, times(0)).commitSync()
                }

                "then no attempts were made to handle messages" {
                    verify(messageHandler, times(0)).apply {
                        handle(any<IncomingMessage<String>>())
                    }
                }
            }
        }
    }

    private fun messages(vararg items: IncomingMessage<String>): IncomingMessages<String> =
        object : IncomingMessages<String> {
            private val ITEMS: List<IncomingMessage<String>> = items.toList()
            override val isEmpty: Boolean = false
            override fun iterator(): Iterator<IncomingMessage<String>> = this.ITEMS.iterator()
        }

    companion object {
        private const val FIRST_TOPIC = "topic-1"
        private val FIRST_MESSAGE_KEY = MessageRoutingKey.of("key-1")
        private const val FIRST_MESSAGE_BODY = "message-1"
        private const val FIRST_MESSAGE_PARTITION = 1

        private const val SECOND_TOPIC = "topic-2"
        private val SECOND_MESSAGE_KEY = MessageRoutingKey.of("key-2")
        private const val SECOND_MESSAGE_BODY = "message-1"
        private const val SECOND_MESSAGE_PARTITION = 2

        private val FIRST_MESSAGE = IncomingMessage(
            routingKey = FIRST_MESSAGE_KEY,
            body = FIRST_MESSAGE_BODY,
            channel = IncomingMessage.Channel(name = FIRST_TOPIC, partition = FIRST_MESSAGE_PARTITION),
            headers = Headers.EMPTY
        )

        private val SECOND_MESSAGE = IncomingMessage(
            routingKey = SECOND_MESSAGE_KEY,
            body = SECOND_MESSAGE_BODY,
            channel = IncomingMessage.Channel(name = SECOND_TOPIC, partition = SECOND_MESSAGE_PARTITION),
            headers = Headers.EMPTY
        )
    }
}
