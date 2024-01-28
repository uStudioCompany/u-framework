package io.github.ustudiocompany.uframework.saga.message.header

import io.github.airflux.functional.Result
import io.github.airflux.functional.flatMap
import io.github.airflux.functional.mapError
import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.HeaderErrors
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import io.github.ustudiocompany.uframework.saga.message.header.type.Status
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.status(value: Status): Header =
    Header(STATUS_HEADER_NAME, value.key.toByteArray(UTF_8))

public fun Headers.status(): Result<Status, HeaderErrors> =
    last(STATUS_HEADER_NAME)
        .flatMap {
            Status.of(it.valueAsString())
                .mapError { failure -> HeaderErrors.InvalidValue(it.name, failure) }
        }

public const val STATUS_HEADER_NAME: String = "status"
