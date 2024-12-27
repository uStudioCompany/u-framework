package io.github.ustudiocompany.uframework.rulesengine.core.rule.context

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.flatMap
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.rulesengine.executor.error.MergeError

internal fun Context.update(
    source: Source,
    action: Step.Result.Action,
    value: DataElement,
    merge: (origin: DataElement, target: DataElement) -> ResultK<DataElement, Failure>
): ExecutionResult =
    when (action) {
        Step.Result.Action.PUT -> this.add(source, value)
        Step.Result.Action.MERGE -> this[source]
            .andThen { origin -> merge(origin, value).mapFailure { MergeError(it) } }
            .andThen { this.replace(source, it) }
    }.flatMap { Success.asNull }
