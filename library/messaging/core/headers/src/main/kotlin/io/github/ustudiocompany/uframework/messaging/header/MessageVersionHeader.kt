package io.github.ustudiocompany.uframework.messaging.header

import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.version(value: MessageVersion): Header =
    Header(MESSAGE_VERSION_HEADER_NAME, value.toString().toByteArray(UTF_8))

public fun Headers.version(): Header? = this.last(MESSAGE_VERSION_HEADER_NAME)

public const val MESSAGE_VERSION_HEADER_NAME: String = "message-version"
