package io.github.ustudiocompany.uframework.messaging.header

import io.github.ustudiocompany.uframework.messaging.header.type.MessageType
import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.type(value: MessageType): Header =
    Header(MESSAGE_TYPE_HEADER_NAME, value.key.toByteArray(UTF_8))

public fun Headers.type(): Header? = this.last(MESSAGE_TYPE_HEADER_NAME)

public const val MESSAGE_TYPE_HEADER_NAME: String = "message-type"