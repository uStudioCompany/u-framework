package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepResult

public fun interface Merger {
    public fun merge(
        strategyCode: StepResult.Action.Merge.StrategyCode,
        dst: DataElement,
        src: DataElement
    ): ResultK<DataElement, Failure>
}
