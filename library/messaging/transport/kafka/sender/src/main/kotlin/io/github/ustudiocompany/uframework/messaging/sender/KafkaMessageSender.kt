package io.github.ustudiocompany.uframework.messaging.sender

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.logging.api.Logging
import io.github.ustudiocompany.uframework.logging.api.debug
import io.github.ustudiocompany.uframework.logging.diagnostic.context.DiagnosticContext
import io.github.ustudiocompany.uframework.logging.diagnostic.context.withDiagnosticContext
import io.github.ustudiocompany.uframework.messaging.message.ChannelName
import io.github.ustudiocompany.uframework.messaging.message.OutgoingMessage
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Duration
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public fun <T> kafkaMessageSenderProps(
    bootstrapServers: List<String>,
    valueSerializer: Serializer<T>,
    acks: KafkaMessageSender.Properties.Asks
): KafkaMessageSender.Properties<T> =
    KafkaMessageSender.Properties(
        bootstrapServers = bootstrapServers,
        serializers = KafkaMessageSender.Properties.Serializers(value = valueSerializer),
        acks = acks
    )

public fun <T : Any> kafkaMessageSender(property: KafkaMessageSender.Properties<T>): MessageSender<T> =
    KafkaMessageSender(property)

public class KafkaMessageSender<T : Any>(property: Properties<T>) : MessageSender<T> {

    private val producer = KafkaProducer<String, T>(property.toProperties())

    context (Logging, DiagnosticContext)
    override suspend fun send(
        channelName: ChannelName,
        message: OutgoingMessage<T>
    ): Result<SentMessageMetadata, MessageSender.Errors.Send> =
        suspendCoroutine { cont ->
            withDiagnosticContext(
                SENDER_MESSAGE_CHANNEL_NAME to channelName,
                SENDER_MESSAGE_ROUTING_KEY to message.routingKey
            ) {
                logger.debug { "Sending a message to a channel." }
                val recordHeaders: List<RecordHeader> =
                    message.headers.map { RecordHeader(it.name, it.valueAsByteArray()) }
                val record = ProducerRecord(channelName.get, null, message.routingKey.get, message.body, recordHeaders)
                producer.send(record) { metadata, exception ->
                    val result = if (exception == null)
                        KafkaSentMessageMetadata(metadata).success()
                    else
                        MessageSender.Errors.Send(
                            channel = channelName,
                            key = message.routingKey,
                            exception = exception
                        ).error()

                    cont.resume(result)
                }
            }
        }

    public class Properties<T>(
        public var bootstrapServers: List<String>,
        public val serializers: Serializers<T>,
        public var acks: Asks,
        public val clientId: String? = null,
        public var batchSize: Int? = null,
        public var bufferMemory: Long? = null,
        public var compressionType: CompressionType? = null,
        public var retries: Retries? = null,
        public var periods: Periods? = null,
        public var properties: Map<String, String> = HashMap()
    ) {

        public fun toProperties(): java.util.Properties = Properties().apply {
            add(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.joinToString())
            add(ProducerConfig.CLIENT_ID_CONFIG, clientId)
            add(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializers.key::class.java.name)
            add(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializers.value::class.java.name)
            add(ProducerConfig.ACKS_CONFIG, acks.get)
            add(ProducerConfig.BATCH_SIZE_CONFIG, batchSize)
            add(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory)
            add(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType)

            retries?.also {
                add(ProducerConfig.RETRIES_CONFIG, it.count)
                add(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, it.retryBackoff)
            }

            periods?.also {
                add(ProducerConfig.MAX_BLOCK_MS_CONFIG, it.maxBlock)
                add(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, it.deliveryTimeout)
                add(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, it.requestTimeout)
                add(ProducerConfig.LINGER_MS_CONFIG, it.linger)
            }

            if (properties.isNotEmpty()) putAll(properties)
        }

        public class Serializers<T>(
            public val value: Serializer<T>
        ) {
            public val key: Serializer<String> = StringSerializer()
        }

        public enum class Asks(public val get: String) {
            NONE("0"),
            LOCAL_LOG("1"),
            ALL("all")
        }

        public enum class CompressionType(public val get: String) {
            NONE("none"),
            GZIP("gzip"),
            LZ4("lz4"),
            SNAPPY("snappy"),
            ZSTD("zstd");

            override fun toString(): String = get
        }

        public class Retries(
            public val count: Int? = null,
            public val retryBackoff: Milliseconds? = null,
        )

        public class Periods(
            public val maxBlock: Milliseconds? = null,
            public val deliveryTimeout: Milliseconds? = null,
            public val requestTimeout: Milliseconds? = null,
            public val linger: Milliseconds? = null,
        )

        @JvmInline
        public value class Milliseconds(private val get: Long) {
            public constructor(value: Duration) : this(value.toMillis())

            override fun toString(): String = get.toString()
        }

        public companion object {
            private fun java.util.Properties.add(name: String, value: Any?): java.util.Properties =
                apply { if (value != null) this[name] = value.toString() }
        }
    }

    private companion object {
        private const val SENDER_MESSAGE_CHANNEL_NAME = "sender-message-channel-name"
        private const val SENDER_MESSAGE_ROUTING_KEY = "sender-message-routing-key"
    }
}
