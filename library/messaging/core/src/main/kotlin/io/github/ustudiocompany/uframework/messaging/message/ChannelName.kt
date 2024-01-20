package io.github.ustudiocompany.uframework.messaging.message

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.ACTUAL_VALUE_DETAIL_KEY
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.PATTERN_DETAIL_KEY
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

@JvmInline
public value class ChannelName private constructor(public val get: String) {

    public companion object {

        public fun of(value: String): Result<ChannelName, Errors> =
            if (value.matches(regex))
                ChannelName(value.lowercase()).success()
            else
                Errors.InvalidFormat(value = value).error()

        private const val PATTERN: String = """^[a-zA-Z]([a-zA-Z0-9\-])*$"""
        private val regex = PATTERN.toRegex()
    }

    public sealed class Errors : TypeFailure<ChannelName> {
        override val type: TypeOf<ChannelName>
            get() = typeOf<ChannelName>()

        public class InvalidFormat(value: String) : Errors() {
            override val number: String = "1"

            override val description: String = "The value `$value` mismatches the pattern `$PATTERN`."

            override val details: Failure.Details = Failure.Details.of(
                PATTERN_DETAIL_KEY to PATTERN,
                ACTUAL_VALUE_DETAIL_KEY to value
            )
        }
    }
}
