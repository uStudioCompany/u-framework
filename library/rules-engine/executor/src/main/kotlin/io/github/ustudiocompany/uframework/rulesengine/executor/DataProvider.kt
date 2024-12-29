package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import java.net.URI

public fun interface DataProvider {
    public fun call(request: Request): ResultK<DataElement, Failure>

    public class Request(
        public val uri: URI,
        public val headers: List<Header>
    ) {

        public data class Header(
            public val name: String,
            public val value: String
        )
    }
}
