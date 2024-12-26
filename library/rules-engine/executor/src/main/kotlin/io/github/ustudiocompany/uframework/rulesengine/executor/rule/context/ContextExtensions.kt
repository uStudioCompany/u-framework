package io.github.ustudiocompany.uframework.rulesengine.executor.rule.context

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.flatMap
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.error.MergeError

internal fun Context.apply(
    source: Source,
    action: Step.Result.Action,
    value: DataElement,
    merger: Merger
): ExecutionResult =
    when (action) {
        Step.Result.Action.PUT -> this.insert(source, value)
        Step.Result.Action.MERGE -> this[source]
            .andThen { origin -> merger.merge(origin, value).mapFailure { MergeError(it) } }
            .andThen { this.update(source, it) }
    }.flatMap { Success.asNull }
