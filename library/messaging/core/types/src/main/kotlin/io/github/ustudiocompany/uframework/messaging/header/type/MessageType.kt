package io.github.ustudiocompany.uframework.messaging.header.type

import io.github.ustudiocompany.uframework.utils.EnumElementProvider

public enum class MessageType(override val key: String) : EnumElementProvider.Key {
    COMMAND(key = "command"),
    REPLY(key = "reply");

    public companion object : EnumElementProvider<MessageType>(info())
}
