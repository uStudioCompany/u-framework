package io.github.ustudiocompany.uframework.rulesengine.core.rule.context

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.flatMap
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult

internal fun Context.update(
    source: Source,
    action: Step.Result.Action,
    value: DataElement,
    merge: (DataElement, DataElement) -> ResultK<DataElement, Failure>
): ExecutionResult = when (action) {
    Step.Result.Action.PUT -> this.add(source, value)
    Step.Result.Action.MERGE -> this.merge(source, value, merge)
}.flatMap { Success.asNull }
