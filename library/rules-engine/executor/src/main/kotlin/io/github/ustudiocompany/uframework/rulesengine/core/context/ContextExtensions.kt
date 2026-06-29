package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.asError
import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.asSome
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.mapFail
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineIncident
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepResult
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger

internal operator fun Context.get(source: Source): ResultK<JsonElement, ContextErrors.SourceMissing> =
    getOrNull(source)
        ?.asSuccess()
        ?: ContextErrors.SourceMissing(source).asFailure()

internal fun Context.update(
    source: Source,
    action: StepResult.Action,
    value: JsonElement,
    merge: Merger
): Maybe<Fail<ContextErrors, ContextIncident>> =
    when (action) {
        is StepResult.Action.Put -> put(source = source, value = value)
        is StepResult.Action.Replace -> replace(source = source, value = value)
        is StepResult.Action.Merge -> merge(
            source = source,
            value = value,
            strategyCode = action.strategyCode,
            merge = merge
        )
    }

internal fun Context.put(source: Source, value: JsonElement): Maybe<Fail.Error<ContextErrors.SourceAlreadyExists>> {
    val isAdded = putIfAbsent(source, value)
    return if (isAdded)
        Maybe.none()
    else
        ContextErrors.SourceAlreadyExists(source).asError().asSome()
}

internal fun Context.replace(source: Source, value: JsonElement): Maybe<Fail.Error<ContextErrors.DataReplacement>> {
    val isReplaced = putIfPresent(source, value)
    return if (isReplaced)
        Maybe.none()
    else
        ContextErrors.DataReplacement(source).asError().asSome()
}

internal fun Context.merge(
    source: Source,
    value: JsonElement,
    strategyCode: StepResult.Action.Merge.StrategyCode,
    merge: Merger
): Maybe<Fail<ContextErrors, ContextIncident>> =
    maybeFailure {
        val context = this@merge
        val (origin) = context[source].mapFailure { it.asError() }
        val (updated) = merge.merge(strategyCode, origin, value)
            .mapFail(
                onError = { error -> ContextErrors.DataMerge(source = source, cause = error) },
                onException = { incident -> ContextIncident.DataMerge(source = source, cause = incident) }
            )

        val isReplaced = putIfPresent(source, updated)
        return if (isReplaced)
            Maybe.none()
        else
            ContextErrors.DataReplacement(source).asError().asSome()
    }

private const val DETAILS_KEY_SOURCE = "source-name"

internal sealed interface ContextErrors : BasicRulesEngineError {

    class SourceMissing(source: Source) : ContextErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "The source '${source.get}' is not found in the context."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class SourceAlreadyExists(source: Source) : ContextErrors {
        override val code: String = PREFIX + "2"
        override val description: String =
            "Error adding data to context by source '${source.get}'. Source is already exists."
        override val cause: Failure.Cause = Failure.Cause.None
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class DataReplacement(source: Source) : ContextErrors {
        override val code: String = PREFIX + "3"
        override val description: String =
            "Error replacing data in the context by source '${source.get}'. Source is missing."
        override val cause: Failure.Cause = Failure.Cause.None
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class DataMerge(source: Source, cause: Merger.Error) : ContextErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Error merging data in the context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "UPDATE-DATA-IN-CONTEXT-ERROR-"
    }
}

internal sealed interface ContextIncident : BasicRulesEngineIncident {

    class DataMerge(source: Source, cause: Merger.Incident) : ContextIncident {
        override val code: String = PREFIX + "1"
        override val description: String = "Incident updating data in the context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "UPDATE-DATA-IN-CONTEXT-INCIDENT-"
    }
}
