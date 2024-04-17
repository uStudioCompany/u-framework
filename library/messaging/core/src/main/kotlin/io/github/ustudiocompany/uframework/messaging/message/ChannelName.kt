package io.github.ustudiocompany.uframework.messaging.message

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.success
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

@JvmInline
public value class ChannelName private constructor(public val get: String) {

    public companion object {

        public fun of(value: String): Result<ChannelName, Errors> =
            if (value.isNotBlank())
                ChannelName(value).success()
            else
                Errors.IsBlank.failure()
    }

    public sealed class Errors : TypeFailure<ChannelName> {
        override val type: TypeOf<ChannelName>
            get() = typeOf<ChannelName>()

        public data object IsBlank : Errors() {
            override val number: String = "1"
            override val description: String = "The value of the channel name is blank."
        }
    }
}
