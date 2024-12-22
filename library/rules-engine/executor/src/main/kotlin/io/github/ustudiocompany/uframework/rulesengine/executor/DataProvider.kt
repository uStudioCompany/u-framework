package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError
import java.net.URI

public fun interface DataProvider {
    public fun call(uri: URI, headers: Map<String, DataElement>): ResultK<DataElement, RuleEngineError>
}
