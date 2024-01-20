package io.github.ustudiocompany.uframework.saga.message.header

import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import io.github.ustudiocompany.uframework.saga.message.header.type.MessageId
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.messageId(value: MessageId): Header =
    Header(MESSAGE_ID_HEADER_NAME, value.get.toByteArray(UTF_8))

public fun Headers.messageId(): Header? = this.last(MESSAGE_ID_HEADER_NAME)

private const val MESSAGE_ID_HEADER_NAME: String = "message-id"
