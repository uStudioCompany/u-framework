package io.github.ustudiocompany.uframework.saga.message.header

import io.github.airflux.functional.Result
import io.github.airflux.functional.flatMap
import io.github.airflux.functional.mapError
import io.github.ustudiocompany.uframework.messaging.message.header.Header
import io.github.ustudiocompany.uframework.messaging.message.header.HeaderErrors
import io.github.ustudiocompany.uframework.messaging.message.header.Headers
import io.github.ustudiocompany.uframework.saga.message.header.type.CorrelationId
import kotlin.text.Charsets.UTF_8

public fun Header.Companion.correlationId(value: CorrelationId): Header =
    Header(CORRELATION_ID_HEADER_NAME, value.get.toByteArray(UTF_8))

public fun Headers.correlationId(): Result<CorrelationId, HeaderErrors> =
    last(CORRELATION_ID_HEADER_NAME)
        .flatMap {
            CorrelationId.of(it.valueAsString())
                .mapError { failure -> HeaderErrors.InvalidValue(it.name, failure) }
        }

public const val CORRELATION_ID_HEADER_NAME: String = "correlation-id"
