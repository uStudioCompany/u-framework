package io.github.ustudiocompany.uframework.saga.message.header

import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import io.github.ustudiocompany.uframework.saga.message.header.type.Status
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.status(value: Status): Header =
    Header(STATUS_HEADER_NAME, value.key.toByteArray(UTF_8))

public fun Headers.status(): Header? = this.last(STATUS_HEADER_NAME)

public const val STATUS_HEADER_NAME: String = "status"
