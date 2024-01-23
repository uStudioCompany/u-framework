package io.github.ustudiocompany.uframework.messaging.header

import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.messageId(value: MessageId): Header =
    Header(MESSAGE_ID_HEADER_NAME, value.get.toByteArray(UTF_8))

public fun Headers.messageId(): Header? = this.last(MESSAGE_ID_HEADER_NAME)

public const val MESSAGE_ID_HEADER_NAME: String = "message-id"
