package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public fun interface CallProvider {

    public fun call(method: Method, args: List<Arg>): ResultK<DataElement, Failure>

    @JvmInline
    public value class Method(public val get: String)

    public data class Arg(
        public val name: String,
        public val value: String
    )
}
