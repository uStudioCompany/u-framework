package io.github.ustudiocompany.uframework.rulesengine.core.context

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.asSome
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Source
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.StepResult

internal fun Context.update(
    source: Source,
    action: StepResult.Action,
    value: DataElement,
    merge: (StepResult.Action.Merge.StrategyCode, dst: DataElement, src: DataElement) -> ResultK<DataElement, Failure>
): Maybe<UpdateContextErrors> =
    when (action) {
        is StepResult.Action.Put -> tryAdd(source = source, value = value)
            .map { failure ->
                UpdateContextErrors.AddingData(source = source, cause = failure)
            }

        is StepResult.Action.Replace -> tryReplace(source = source, value = value)
            .map { failure ->
                UpdateContextErrors.ReplacingData(source = source, cause = failure)
            }

        is StepResult.Action.Merge -> tryMerge(
            source = source,
            value = value,
            strategyCode = action.strategyCode,
            merge = merge
        ).map { failure ->
            UpdateContextErrors.MergingData(source = source, cause = failure)
        }
    }

internal sealed interface UpdateContextErrors : BasicRulesEngineError {

    class AddingData(source: Source, cause: AddDataToContextErrors) : UpdateContextErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error adding data to context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class ReplacingData(source: Source, cause: ReplaceDataInContextErrors) : UpdateContextErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error replacing data in context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class MergingData(source: Source, cause: MergeDataInContextErrors) : UpdateContextErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error merging data in context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "UPDATE-DATA-IN-CONTEXT-"
    }
}

internal fun Context.tryGet(source: Source): ResultK<DataElement, GetDataFromContextErrors> {
    val value = this[source]
    return value?.asSuccess()
        ?: GetDataFromContextErrors.SourceMissing(source).asFailure()
}

internal sealed interface GetDataFromContextErrors : BasicRulesEngineError {

    class SourceMissing(source: Source) : GetDataFromContextErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "The source '${source.get}' is not found in context."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "GET-DATA-FROM-CONTEXT-"
    }
}

internal fun Context.tryAdd(source: Source, value: DataElement): Maybe<AddDataToContextErrors.SourceAlreadyExists> {
    val isAdded = add(source, value)
    return if (isAdded)
        Maybe.none()
    else
        AddDataToContextErrors.SourceAlreadyExists(source).asSome()
}

internal sealed interface AddDataToContextErrors : BasicRulesEngineError {

    class SourceAlreadyExists(source: Source) : AddDataToContextErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "The source '${source.get}' is already exists in context."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "ADD-DATA-TO-CONTEXT-"
    }
}

internal fun Context.tryReplace(source: Source, value: DataElement): Maybe<ReplaceDataInContextErrors> {
    val isReplaced = replace(source, value)
    return if (isReplaced)
        Maybe.none()
    else
        ReplaceDataInContextErrors.SourceMissing(source).asSome()
}

internal sealed interface ReplaceDataInContextErrors : BasicRulesEngineError {

    class SourceMissing(source: Source) : ReplaceDataInContextErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "The source '${source.get}' is not found in context."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "GET-DATA-FROM-CONTEXT-"
    }
}

internal fun Context.tryMerge(
    source: Source,
    value: DataElement,
    strategyCode: StepResult.Action.Merge.StrategyCode,
    merge: (StepResult.Action.Merge.StrategyCode, dst: DataElement, src: DataElement) -> ResultK<DataElement, Failure>
): Maybe<MergeDataInContextErrors> =
    maybeFailure {
        val (origin) = tryGet(source = source)
            .mapFailure { failure -> MergeDataInContextErrors.GettingDataFromContext(source = source, cause = failure) }
        val (updated) = merge(strategyCode, origin, value)
            .mapFailure { failure -> MergeDataInContextErrors.MergingData(cause = failure) }
        tryReplace(source = source, value = updated)
            .map { failure -> MergeDataInContextErrors.ReplacingDataInContext(source = source, cause = failure) }
    }

internal sealed interface MergeDataInContextErrors : BasicRulesEngineError {

    class GettingDataFromContext(source: Source, cause: GetDataFromContextErrors) : MergeDataInContextErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error getting data from the context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class MergingData(cause: Failure) : MergeDataInContextErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "The error of merging data."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ReplacingDataInContext(source: Source, cause: ReplaceDataInContextErrors) : MergeDataInContextErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error replacing data in context by source '${source.get}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    private companion object {
        private const val PREFIX = "MERGE-DATA-IN-CONTEXT-"
    }
}

private const val DETAILS_KEY_SOURCE = "source-name"
