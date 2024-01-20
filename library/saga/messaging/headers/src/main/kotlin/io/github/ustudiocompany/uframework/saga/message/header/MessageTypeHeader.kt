package io.github.ustudiocompany.uframework.saga.message.header

import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import io.github.ustudiocompany.uframework.saga.message.header.type.MessageType
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.type(value: MessageType): Header =
    Header(MESSAGE_TYPE_HEADER_NAME, value.key.toByteArray(UTF_8))

public fun Headers.type(): Header? = this.last(MESSAGE_TYPE_HEADER_NAME)

private const val MESSAGE_TYPE_HEADER_NAME: String = "message-type"
