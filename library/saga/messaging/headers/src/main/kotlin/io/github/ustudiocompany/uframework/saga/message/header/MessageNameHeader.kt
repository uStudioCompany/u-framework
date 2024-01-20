package io.github.ustudiocompany.uframework.saga.message.header

import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import io.github.ustudiocompany.uframework.saga.message.header.type.MessageName
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.name(value: MessageName): Header =
    Header(MESSAGE_NAME_HEADER_NAME, value.name.toByteArray(UTF_8))

public fun Headers.name(): Header? = this.last(MESSAGE_NAME_HEADER_NAME)

private const val MESSAGE_NAME_HEADER_NAME: String = "message-name"
