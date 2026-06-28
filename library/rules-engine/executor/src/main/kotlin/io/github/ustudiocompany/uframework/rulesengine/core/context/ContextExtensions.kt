package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.asError
import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.asSome
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.mapFail
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

internal fun Context.update(
    source: Source,
    action: StepResult.Action,
    value: JsonElement,
    merge: Merger
): Maybe<Fail<ContextUpdateErrors, ContextUpdateIncident>> =
    when (action) {
        is StepResult.Action.Put -> put(source = source, value = value)
            .map { failure -> ContextUpdateErrors.DataAddition(source = source, cause = failure).asError() }

        is StepResult.Action.Replace -> replace(source = source, value = value)
            .map { failure -> ContextUpdateErrors.DataReplacement(source = source, cause = failure).asError() }

        is StepResult.Action.Merge -> merge(
            source = source,
            value = value,
            strategyCode = action.strategyCode,
            merge = merge
        ).mapFail(
            onError = { error -> ContextUpdateErrors.DataMerge(source = source, cause = error) },
            onException = { incident -> ContextUpdateIncident.DataMerge(source = source, cause = incident) }
        )
    }

internal sealed interface ContextUpdateErrors : BasicRulesEngineError {

    class DataAddition(source: Source, cause: ContextDataAdditionErrors) : ContextUpdateErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error adding data to context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class DataReplacement(source: Source, cause: ContextDataReplacementErrors) : ContextUpdateErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error replacing data in the context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class DataMerge(source: Source, cause: ContextDataMergeErrors) : ContextUpdateErrors {
        override val code: String = PREFIX + "3"
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

internal sealed interface ContextUpdateIncident : BasicRulesEngineIncident {

    class DataMerge(source: Source, cause: ContextDataMergeIncident) : ContextUpdateIncident {
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

internal operator fun Context.get(source: Source): ResultK<JsonElement, ContextDataRetrievalErrors.SourceMissing> =
    getOrNull(source)
        ?.asSuccess()
        ?: ContextDataRetrievalErrors.SourceMissing(source).asFailure()

internal sealed interface ContextDataRetrievalErrors : BasicRulesEngineError {

    class SourceMissing(source: Source) : ContextDataRetrievalErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "The source '${source.get}' is not found in the context."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "RETRIEVE-DATA-FROM-CONTEXT-ERROR-"
    }
}

internal fun Context.put(source: Source, value: JsonElement): Maybe<ContextDataAdditionErrors.SourceAlreadyExists> {
    val isAdded = putIfAbsent(source, value)
    return if (isAdded)
        Maybe.none()
    else
        ContextDataAdditionErrors.SourceAlreadyExists(source).asSome()
}

internal sealed interface ContextDataAdditionErrors : BasicRulesEngineError {

    class SourceAlreadyExists(source: Source) : ContextDataAdditionErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "The source '${source.get}' is already exists in the context."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "ADD-DATA-TO-CONTEXT-ERROR-"
    }
}

internal fun Context.replace(
    source: Source,
    value: JsonElement
): Maybe<ContextDataReplacementErrors.SourceMissing> {
    val isReplaced = putIfPresent(source, value)
    return if (isReplaced)
        Maybe.none()
    else
        ContextDataReplacementErrors.SourceMissing(source).asSome()
}

internal sealed interface ContextDataReplacementErrors : BasicRulesEngineError {

    class SourceMissing(source: Source) : ContextDataReplacementErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "The source '${source.get}' is not found in the context."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "REPLACE-DATA-FROM-CONTEXT-ERROR-"
    }
}

internal fun Context.merge(
    source: Source,
    value: JsonElement,
    strategyCode: StepResult.Action.Merge.StrategyCode,
    merge: Merger
): Maybe<Fail<ContextDataMergeErrors, ContextDataMergeIncident>> =
    maybeFailure {
        val context = this@merge
        val (origin) = context[source]
            .mapFailure { failure -> ContextDataMergeErrors.DataRetrieval(source = source, cause = failure).asError() }
        val (updated) = merge.merge(strategyCode, origin, value)
            .mapFail(
                onError = { failure -> ContextDataMergeErrors.DataMerge(cause = failure) },
                onException = { failure -> ContextDataMergeIncident.DataMerge(cause = failure) }
            )

        replace(source = source, value = updated)
            .map { failure -> ContextDataMergeErrors.DataReplacement(source = source, cause = failure).asError() }
    }

internal sealed interface ContextDataMergeErrors : BasicRulesEngineError {

    class DataRetrieval(source: Source, cause: ContextDataRetrievalErrors) : ContextDataMergeErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error getting data from the context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class DataMerge(cause: Failure) : ContextDataMergeErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Merging data error."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class DataReplacement(source: Source, cause: ContextDataReplacementErrors) : ContextDataMergeErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error replacing data in the context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "MERGE-DATA-IN-CONTEXT-ERROR-"
    }
}

internal sealed interface ContextDataMergeIncident : BasicRulesEngineIncident {

    class DataMerge(cause: Failure) : ContextDataMergeIncident {
        override val code: String = PREFIX + "1"
        override val description: String = "Merging data incident."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "MERGE-DATA-IN-CONTEXT-INCIDENT-"
    }
}

private const val DETAILS_KEY_SOURCE = "source-name"
