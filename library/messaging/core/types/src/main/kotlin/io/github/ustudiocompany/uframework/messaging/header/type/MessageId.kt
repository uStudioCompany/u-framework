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
import java.util.*

@JvmInline
public value class MessageId private constructor(public val get: String) {

    public sealed class Errors : TypeFailure<MessageId> {

        override val type: TypeOf<MessageId>
            get() = typeOf<MessageId>()

        public class InvalidFormat(value: String) : Errors() {
            override val number: String = "1"

            override val description: String = "The value is an invalid format."

            override val details: Failure.Details = Failure.Details.of(
                PATTERN_DETAIL_KEY to PATTERN,
                ACTUAL_VALUE_DETAIL_KEY to value
            )
        }
    }

    public companion object {

        public fun generate(base: String): MessageId {
            val uuid = UUID.nameUUIDFromBytes(base.toByteArray())
            return MessageId(uuid.toString())
        }

        public fun of(value: String): Result<MessageId, Errors> =
            if (value.matches(regex))
                MessageId(get = value).success()
            else
                Errors.InvalidFormat(value = value).error()

        private const val PATTERN: String =
            """^[0-9a-fA-F]{8}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{12}${'$'}"""
        private val regex = PATTERN.toRegex()
    }
}
