package io.github.ustudiocompany.uframework.messaging.receiver

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

public fun <T> kafkaMessageReceiverFactoryProps(
    bootstrapServers: List<String>,
    groupId: String,
    valueDeserializer: Deserializer<T>,
): KafkaMessageReceiverFactory.Properties<T> =
    KafkaMessageReceiverFactory.Properties(
        bootstrapServers = bootstrapServers,
        deserializers = KafkaMessageReceiverFactory.Properties.Deserializers(value = valueDeserializer),
        groupId = groupId,
        autoCommit = KafkaMessageReceiverFactory.Properties.AutoCommit.No,
        autoOffsetReset = KafkaMessageReceiverFactory.Properties.AutoOffsetReset.EARLIEST
    )

public fun <T> kafkaMessageReceiverFactory(
    properties: KafkaMessageReceiverFactory.Properties<T>
): MessageReceiverFactory<T> = KafkaMessageReceiverFactory {
    KafkaMessageReceiver<T>(
        KafkaConsumer(properties.toProperties(), properties.deserializers.key, properties.deserializers.value),
        properties.pool.timeout
    )
}

public fun interface KafkaMessageReceiverFactory<T> : MessageReceiverFactory<T> {

    public data class Properties<T>(
        public val bootstrapServers: List<String>,
        public val groupId: String,
        public val deserializers: Deserializers<T>,
        public val pool: Pool = Pool(),
        public val autoCommit: AutoCommit,
        public val autoOffsetReset: AutoOffsetReset,
        public val clientId: String? = null,
        public val fetch: Fetch? = null,
        public val heartbeatInterval: Milliseconds? = null,
        public val properties: Map<String, String> = emptyMap()
    ) {

        public fun toProperties(): java.util.Properties = Properties().apply {
            add(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.joinToString())
            add(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializers.key::class.java.name)
            add(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializers.value::class.java.name)
            add(ConsumerConfig.GROUP_ID_CONFIG, groupId)

            when (autoCommit) {
                is AutoCommit.Yes -> {
                    add(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true)
                    add(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommit.interval)
                }

                is AutoCommit.No -> add(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
            }

            add(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset.get)
            add(ConsumerConfig.CLIENT_ID_CONFIG, clientId)
            add(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, pool.maxRecords)
            fetch?.also {
                add(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, it.maxWait)
                add(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, it.minSize)
            }

            add(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatInterval)
            if (properties.isNotEmpty()) putAll(properties)
        }

        public class Deserializers<T>(public val value: Deserializer<T>) {
            public val key: Deserializer<String?> = StringDeserializer()
        }

        public sealed class AutoCommit {
            public data object No : AutoCommit()
            public class Yes(public val interval: Duration) : AutoCommit()
        }

        public enum class AutoOffsetReset(internal val get: String) {
            EARLIEST("earliest"),
            LATEST("latest")
        }

        public class Pool(
            public val timeout: Duration = Duration.ofMillis(POLL_TIMEOUT),
            public val maxRecords: Int? = null,
        )

        public class Fetch(
            public val maxWait: Milliseconds? = null,
            public val minSize: Int? = null,
        )

        @JvmInline
        public value class Milliseconds(private val get: Long) {
            public constructor(value: Duration) : this(value.toMillis())

            override fun toString(): String = get.toString()
        }

        public companion object {
            private const val POLL_TIMEOUT = 100L

            private fun java.util.Properties.add(name: String, value: Any?): java.util.Properties =
                apply { if (value != null) this[name] = value.toString() }
        }
    }
}
