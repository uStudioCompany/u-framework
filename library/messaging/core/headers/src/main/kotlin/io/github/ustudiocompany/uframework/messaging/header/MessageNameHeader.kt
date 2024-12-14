package io.github.ustudiocompany.uframework.messaging.header

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.flatMap
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.messaging.header.type.MessageName
import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.HeaderErrors
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.name(value: MessageName): Header =
    Header(MESSAGE_NAME_HEADER_NAME, value.name.toByteArray(UTF_8))

public fun Headers.name(): ResultK<MessageName, HeaderErrors> =
    last(MESSAGE_NAME_HEADER_NAME)
        .flatMap {
            MessageName.of(it.valueAsString())
                .mapFailure { failure -> HeaderErrors.InvalidValue(it.name, failure) }
        }

public const val MESSAGE_NAME_HEADER_NAME: String = "message-name"
