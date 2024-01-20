package io.github.ustudiocompany.uframework.messaging.message

@JvmInline
public value class MessageRoutingKey private constructor(public val get: String) {

    public companion object {
        public fun of(value: String?): MessageRoutingKey = MessageRoutingKey(value ?: "none")
    }
}
