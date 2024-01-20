package io.github.ustudiocompany.uframework.messaging.sender

import org.apache.kafka.clients.producer.RecordMetadata

@JvmInline
internal value class KafkaSentMessageMetadata(private val recordMetadata: RecordMetadata) : SentMessageMetadata {
    override val offset: Long
        get() = recordMetadata.offset()

    override val hasTimestamp: Boolean
        get() = recordMetadata.hasTimestamp()

    override val timestamp: Long
        get() = recordMetadata.timestamp()

    override val topic: String
        get() = recordMetadata.topic()

    override val partition: Int
        get() = recordMetadata.partition()
}
