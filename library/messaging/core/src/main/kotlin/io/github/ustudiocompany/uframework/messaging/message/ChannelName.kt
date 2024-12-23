package io.github.ustudiocompany.uframework.messaging.message

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.failure.TypeFailure
import io.github.ustudiocompany.uframework.failure.TypeOf
import io.github.ustudiocompany.uframework.failure.typeOf

@JvmInline
public value class ChannelName private constructor(public val get: String) {

    public companion object {

        public fun of(value: String): ResultK<ChannelName, Errors> =
            if (value.isNotBlank())
                ChannelName(value).asSuccess()
            else
                Errors.IsBlank.asFailure()
    }

    public sealed class Errors : TypeFailure<ChannelName> {
        override val type: TypeOf<ChannelName>
            get() = typeOf<ChannelName>()

        public data object IsBlank : Errors() {
            override val code: String = type.name + "1"
            override val description: String = "The value of the channel name is blank."
        }
    }
}
