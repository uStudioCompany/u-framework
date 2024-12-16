package io.github.ustudiocompany.uframework.messaging.header.type

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.ACTUAL_VALUE_DETAIL_KEY
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.PATTERN_DETAIL_KEY
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

@JvmInline
public value class CorrelationId private constructor(public val get: String) {

    public sealed class Errors : TypeFailure<CorrelationId> {

        override val type: TypeOf<CorrelationId>
            get() = typeOf<CorrelationId>()

        public class InvalidFormat(value: String) : Errors() {
            override val number: String = "1"
            override val description: String = "Value has an invalid format."
            override val details: Failure.Details = Failure.Details.of(
                PATTERN_DETAIL_KEY to PATTERN,
                ACTUAL_VALUE_DETAIL_KEY to value
            )
        }
    }

    public companion object {
        private const val PATTERN: String =
            """^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"""
        private val REGEX = PATTERN.toRegex()

        public fun of(value: String): ResultK<CorrelationId, Errors> =
            if (value.matches(REGEX))
                CorrelationId(get = value).asSuccess()
            else
                Errors.InvalidFormat(value = value).asFailure()
    }
}
