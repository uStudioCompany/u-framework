package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.data.toStringValue
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

public fun interface CallProvider {

    public fun call(uri: Uri, args: List<Arg>, body: Body?): ResultK<JsonElement, Error>

    @JvmInline
    public value class Uri private constructor(public val get: String) {

        public companion object {
            public fun from(value: String): Uri = Uri(URLEncoder.encode(value, UTF_8))
        }
    }

    public data class Arg(
        public val name: String,
        public val value: String
    )

    @JvmInline
    public value class Body(private val value: JsonElement) {
        public fun asJson(): String = value.toStringValue()
    }

    public class Error private constructor(
        message: String = "",
        override val cause: Failure.Cause = Failure.Cause.None,
        override val details: Failure.Details = Failure.Details.NONE
    ) : BasicRulesEngineError {

        public constructor() : this(
            message = "",
            cause = Failure.Cause.None,
            details = Failure.Details.NONE
        )

        public constructor(
            message: String = "",
            cause: Failure,
            details: Failure.Details = Failure.Details.NONE
        ) : this(
            message = message,
            cause = Failure.Cause.Failure(cause),
            details = details
        )

        public constructor(
            message: String = "",
            exception: Throwable? = null,
            details: Failure.Details = Failure.Details.NONE
        ) : this(
            message = message,
            cause = exception.toFailureCause(),
            details = details
        )

        override val code: String = PREFIX + "1"
        override val description: String =
            "The error of http call." + if (message.isNotEmpty()) " $message" else ""

        private companion object {
            private const val PREFIX = "CALL-PROVIDER-"

            private fun Throwable?.toFailureCause(): Failure.Cause =
                this?.let { Failure.Cause.Exception(it) } ?: Failure.Cause.None
        }
    }
}
