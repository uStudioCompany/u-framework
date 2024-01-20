package io.github.ustudiocompany.uframework.messaging.receiver

import io.github.ustudiocompany.uframework.messaging.message.IncomingMessages
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration

internal class KafkaMessageReceiver<T>(
    private val receiver: KafkaConsumer<String?, T>,
    private val pollTimeout: Duration
) : MessageReceiver<T> {

    override fun subscribe(topics: Collection<String>) {
        receiver.subscribe(topics)
    }

    override fun poll(): IncomingMessages<T> =
        KafkaIncomingMessages(receiver.poll(pollTimeout))

    override fun commitAsync() {
        receiver.commitAsync()
    }

    override fun commitSync() {
        receiver.commitSync()
    }

    override fun close() {
        receiver.close()
    }
}
