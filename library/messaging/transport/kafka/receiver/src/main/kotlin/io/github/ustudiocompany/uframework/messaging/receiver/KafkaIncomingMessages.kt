package io.github.ustudiocompany.uframework.messaging.receiver

import io.github.ustudiocompany.uframework.messaging.message.IncomingMessage
import io.github.ustudiocompany.uframework.messaging.message.IncomingMessages
import org.apache.kafka.clients.consumer.ConsumerRecords

internal class KafkaIncomingMessages<T> private constructor(private val records: ConsumerRecords<String?, T>) :
    IncomingMessages<T> {
    override val isEmpty: Boolean
        get() = records.isEmpty

    override fun iterator(): Iterator<IncomingMessage<T>> = MessagesIterator(records)

    private class MessagesIterator<T>(records: ConsumerRecords<String?, T>) : Iterator<IncomingMessage<T>> {
        private val iter = records.iterator()

        override fun hasNext(): Boolean = iter.hasNext()

        override fun next(): IncomingMessage<T> =
            if (iter.hasNext()) iter.next().toInboxMessage() else throw NoSuchElementException()
    }

    companion object {
        operator fun <T> invoke(records: ConsumerRecords<String?, T>): IncomingMessages<T> =
            if (records.isEmpty) IncomingMessages.Empty else KafkaIncomingMessages(records)
    }
}
