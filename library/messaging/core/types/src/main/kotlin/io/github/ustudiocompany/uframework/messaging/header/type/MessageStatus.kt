package io.github.ustudiocompany.uframework.messaging.header.type

import io.github.ustudiocompany.uframework.utils.EnumElementProvider

public enum class MessageStatus(override val key: String) : EnumElementProvider.Key {
    SUCCESS(key = "success"),
    ERROR(key = "error"),
    INCIDENT(key = "incident");

    public companion object : EnumElementProvider<MessageStatus>(info())
}
