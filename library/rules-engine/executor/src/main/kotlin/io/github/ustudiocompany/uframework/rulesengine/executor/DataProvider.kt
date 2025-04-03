package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

public fun interface DataProvider {

    public fun get(uri: Uri, args: List<Arg>): ResultK<DataElement, Failure>

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
}
