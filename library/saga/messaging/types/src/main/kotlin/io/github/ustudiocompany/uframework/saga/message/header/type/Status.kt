package io.github.ustudiocompany.uframework.saga.message.header.type

import io.github.ustudiocompany.uframework.utils.EnumElementProvider

public enum class Status(override val key: String) : EnumElementProvider.Key {
    SUCCESS(key = "success"),
    ERROR(key = "error"),
    INCIDENT(key = "incident");

    public companion object : EnumElementProvider<Status>(info())
}
