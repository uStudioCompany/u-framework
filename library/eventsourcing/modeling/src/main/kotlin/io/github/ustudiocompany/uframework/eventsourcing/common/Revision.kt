package io.github.ustudiocompany.uframework.eventsourcing.common

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeFailure.Companion.ACTUAL_VALUE_DETAIL_KEY
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

@JvmInline
public value class Revision private constructor(public val get: Long) : Comparable<Revision> {

    public fun next(): Revision = Revision(get + 1)

    override fun compareTo(other: Revision): Int = get.compareTo(other.get)

    public companion object {

        @JvmStatic
        public val initial: Revision = Revision(0)

        @JvmStatic
        public fun of(value: Long): ResultK<Revision, Errors> =
            if (value < 0)
                Errors.Negative(value = value).asFailure()
            else
                Revision(value).asSuccess()
    }

    public sealed class Errors : TypeFailure<Revision> {
        override val type: TypeOf<Revision>
            get() = typeOf<Revision>()

        public data class Negative(val value: Long) : Errors() {
            override val number: String get() = "1"

            override val description: String
                get() = "The value is negative."

            override val details: Failure.Details = Failure.Details.of(
                ACTUAL_VALUE_DETAIL_KEY to value.toString()
            )
        }
    }
}
