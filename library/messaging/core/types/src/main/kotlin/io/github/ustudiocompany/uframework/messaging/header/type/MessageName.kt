package io.github.ustudiocompany.uframework.messaging.header.type

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.ACTUAL_VALUE_DETAIL_KEY
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.PATTERN_DETAIL_KEY
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

public class MessageName private constructor(public val name: String) : Comparable<MessageName> {

    override fun compareTo(other: MessageName): Int = name.compareTo(other.name)

    override fun equals(other: Any?): Boolean =
        this === other || (other is MessageName && this.name == other.name)

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = name

    public sealed class Errors : TypeFailure<MessageName> {
        override val type: TypeOf<MessageName>
            get() = typeOf<MessageName>()

        public class InvalidFormat(value: String) : Errors() {
            override val number: String = "1"

            override val description: String = "The value `$value` mismatches the pattern `$PATTERN`."

            override val details: Failure.Details = Failure.Details.of(
                PATTERN_DETAIL_KEY to PATTERN,
                ACTUAL_VALUE_DETAIL_KEY to value
            )
        }
    }

    public companion object {

        public fun of(value: String): Result<MessageName, Errors> =
            if (value.matches(regex))
                MessageName(value.lowercase()).success()
            else
                Errors.InvalidFormat(value = value).error()

        private const val PATTERN: String = """^[a-zA-Z]([a-zA-Z0-9\-])*$"""
        private val regex = PATTERN.toRegex()
    }
}
