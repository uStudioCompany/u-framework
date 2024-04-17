package io.github.ustudiocompany.uframework.messaging.header

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.flatMap
import io.github.airflux.commons.types.result.mapFailure
import io.github.ustudiocompany.uframework.messaging.header.type.MessageVersion
import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.HeaderErrors
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.version(value: MessageVersion): Header =
    Header(MESSAGE_VERSION_HEADER_NAME, value.toString().toByteArray(UTF_8))

public fun Headers.version(): Result<MessageVersion, HeaderErrors> =
    last(MESSAGE_VERSION_HEADER_NAME)
        .flatMap {
            MessageVersion.of(it.valueAsString())
                .mapFailure { failure -> HeaderErrors.InvalidValue(it.name, failure) }
        }

public const val MESSAGE_VERSION_HEADER_NAME: String = "message-version"
