package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.filterNotNull
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.context.GetDataFromContextErrors
import io.github.ustudiocompany.uframework.rulesengine.core.context.tryGet
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataSearchError
import io.github.ustudiocompany.uframework.rulesengine.core.data.search
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpressionError
import io.github.ustudiocompany.uframework.rulesengine.core.feel.evaluateWithContext
import io.github.ustudiocompany.uframework.rulesengine.core.path.Path

internal fun Value.compute(context: Context): ResultK<DataElement, ValueComputeErrors> =
    when (this) {
        is Value.Literal -> fact.asSuccess()

        is Value.Reference -> context.tryGet(source)
            .mapFailure { failure ->
                ValueComputeErrors.GettingDataFromContext(source = source, cause = failure)
            }
            .andThen { element ->
                element.search(path)
                    .mapFailure { failure ->
                        ValueComputeErrors.SearchingDataByPath(path = path, cause = failure)
                    }
                    .filterNotNull { ValueComputeErrors.DataByPathIsNotFound(source = source, path = path) }
            }

        is Value.Expression -> expression.evaluateWithContext(context)
            .mapFailure { failure ->
                ValueComputeErrors.EvaluatingFeelExpression(expression = expression, cause = failure)
            }
    }

internal sealed interface ValueComputeErrors : BasicRulesEngineError {

    class GettingDataFromContext(source: Source, cause: GetDataFromContextErrors) : ValueComputeErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error getting data from context by source '${source.get}' for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class SearchingDataByPath(path: Path, cause: DataSearchError) : ValueComputeErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error searching data by path '${path.text}' for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_PATH to path.text
        )
    }

    class DataByPathIsNotFound(source: Source, path: Path) : ValueComputeErrors {
        override val code: String = PREFIX + "3"
        override val description: String =
            "Error computing value, data in source '${source.get}' and path '${path.text}' is missing."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get,
            DETAILS_KEY_PATH to path.text
        )
    }

    class EvaluatingFeelExpression(expression: FeelExpression, cause: FeelExpressionError) : ValueComputeErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Error evaluating a FEEL expression for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_EXPRESSION to expression.text
        )
    }

    private companion object {
        private const val PREFIX = "VALUE-COMPUTE-"
        private const val DETAILS_KEY_PATH = "json-path"
        private const val DETAILS_KEY_SOURCE = "source-name"
        private const val DETAILS_KEY_EXPRESSION = "feel-expression"
    }
}

internal fun Value.computeOrNull(context: Context): ResultK<DataElement?, OptionalValueComputeErrors> =
    when (this) {
        is Value.Literal -> fact.asSuccess()

        is Value.Reference -> context.tryGet(source)
            .mapFailure { failure ->
                OptionalValueComputeErrors.GettingDataFromContext(source = source, cause = failure)
            }
            .andThen { element ->
                element.search(path)
                    .mapFailure { failure ->
                        OptionalValueComputeErrors.SearchingDataByPath(path = path, cause = failure)
                    }
            }

        is Value.Expression -> expression.evaluateWithContext(context)
            .mapFailure { failure ->
                OptionalValueComputeErrors.EvaluatingFeelExpression(expression = expression, cause = failure)
            }
    }

internal sealed interface OptionalValueComputeErrors : BasicRulesEngineError {

    class GettingDataFromContext(source: Source, cause: GetDataFromContextErrors) : OptionalValueComputeErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error getting data from context by source '${source.get}' for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class SearchingDataByPath(path: Path, cause: DataSearchError) : OptionalValueComputeErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error searching data by path '${path.text}' for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_PATH to path.text
        )
    }

    class EvaluatingFeelExpression(
        expression: FeelExpression,
        cause: FeelExpressionError
    ) : OptionalValueComputeErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error evaluating an expression for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_EXPRESSION to expression.text
        )
    }

    private companion object {
        private const val PREFIX = "OPTIONAL-VALUE-COMPUTE-"
        private const val DETAILS_KEY_PATH = "json-path"
        private const val DETAILS_KEY_SOURCE = "source-name"
        private const val DETAILS_KEY_EXPRESSION = "feel-expression"
    }
}
