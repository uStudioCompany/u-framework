package io.github.ustudiocompany.uframework.messaging.header

import io.github.airflux.functional.Result
import io.github.airflux.functional.flatMap
import io.github.airflux.functional.mapError
import io.github.ustudiocompany.uframework.messaging.header.type.MessageId
import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.HeaderErrors
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.messageId(value: MessageId): Header =
    Header(MESSAGE_ID_HEADER_NAME, value.get.toByteArray(UTF_8))

public fun Headers.messageId(): Result<MessageId, HeaderErrors> =
    last(MESSAGE_ID_HEADER_NAME)
        .flatMap {
            MessageId.of(it.valueAsString())
                .mapError { failure -> HeaderErrors.InvalidValue(it.name, failure) }
        }

public const val MESSAGE_ID_HEADER_NAME: String = "message-id"
