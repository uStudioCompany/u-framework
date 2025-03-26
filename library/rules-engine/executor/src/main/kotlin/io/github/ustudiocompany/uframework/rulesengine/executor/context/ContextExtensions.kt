package io.github.ustudiocompany.uframework.rulesengine.executor.context

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.asSome
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.executor.error.ContextError

internal fun Context.update(
    source: Source,
    action: Step.Result.Action,
    value: DataElement,
    merge: (DataElement, DataElement) -> ResultK<DataElement, Failure>
): Maybe<ContextError> =
    when (action) {
        Step.Result.Action.PUT -> tryAdd(source = source, value = value)
        Step.Result.Action.REPLACE -> tryReplace(source = source, value = value)
        Step.Result.Action.MERGE -> maybeFailure {
            val (origin) = tryGet(source = source)
            val (updated) = merge(origin, value).mapFailure { ContextError.Merge(source, it) }
            tryReplace(source = source, value = updated)
        }
    }

internal fun Context.tryGet(source: Source): ResultK<DataElement, ContextError.SourceMissing> {
    val value = this[source]
    return value?.asSuccess()
        ?: ContextError.SourceMissing(source).asFailure()
}

internal fun Context.tryAdd(source: Source, value: DataElement): Maybe<ContextError.SourceAlreadyExists> {
    val isAdded = add(source, value)
    return if (isAdded)
        Maybe.none()
    else
        ContextError.SourceAlreadyExists(source).asSome()
}

internal fun Context.tryReplace(source: Source, value: DataElement): Maybe<ContextError.SourceMissing> {
    val isReplaced = replace(source, value)
    return if (isReplaced)
        Maybe.none()
    else
        ContextError.SourceMissing(source).asSome()
}
