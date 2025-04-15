package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public fun interface Merger {
    public fun merge(
        mergeStrategyCode: MergeStrategyCode,
        dst: DataElement,
        src: DataElement
    ): ResultK<DataElement, Failure>
}
