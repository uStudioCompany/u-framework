package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

public fun interface Merger {
    public fun merge(origin: DataElement, target: DataElement): ResultK<DataElement, RuleEngineError>
}
