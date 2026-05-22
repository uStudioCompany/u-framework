package io.github.ustudiocompany.uframework.rulesengine.core.rule

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.filterNotNull
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.json.path.Path
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.context.GetDataFromContextErrors
import io.github.ustudiocompany.uframework.rulesengine.core.context.get
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataSearchError
import io.github.ustudiocompany.uframework.rulesengine.core.data.search
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.env.GetValueFromEnvVarsErrors
import io.github.ustudiocompany.uframework.rulesengine.core.env.get
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.build

internal fun Value.compute(envVars: EnvVars, context: Context): ResultK<JsonElement, ValueComputeErrors> =
    when (this) {
        is Value.Literal -> fact.asSuccess()

        is Value.Reference -> context[source]
            .mapFailure { failure -> ValueComputeErrors.GettingDataFromContext(source = source, cause = failure) }
            .andThen { element ->
                element.search(path)
                    .mapFailure { failure -> ValueComputeErrors.SearchingDataByPath(path = path, cause = failure) }
                    .filterNotNull { ValueComputeErrors.DataByPathIsNotFound(source = source, path = path) }
            }

        is Value.Expression -> expression.evaluate(envVars, context)
            .mapFailure { failure -> ValueComputeErrors.EvaluatingFeelExpression(cause = failure) }

        is Value.EnvVars -> envVars[name]
            .mapFailure { failure -> ValueComputeErrors.GettingValueFromEnvVars(name = name, cause = failure) }

        is Value.DataStruct -> this.scheme.build(envVars, context)
            .mapFailure { failure -> ValueComputeErrors.DataStructBuilding(cause = failure) }
    }

internal sealed interface ValueComputeErrors : BasicRulesEngineError {

    class GettingDataFromContext(source: Source, cause: GetDataFromContextErrors) : ValueComputeErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error getting data from context by source '${source.get}' of computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class GettingValueFromEnvVars(
        name: EnvVarName,
        cause: GetValueFromEnvVarsErrors.EnvVarMissing
    ) : ValueComputeErrors {
        override val code: String = PREFIX + "2"
        override val description: String =
            "Error getting value from environment variables by name '${name.get}' of computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_ENV_VAR to name.get
        )
    }

    class SearchingDataByPath(path: Path, cause: DataSearchError) : ValueComputeErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error searching data by path '${path.text}' of computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_PATH to path.text
        )
    }

    class DataByPathIsNotFound(source: Source, path: Path) : ValueComputeErrors {
        override val code: String = PREFIX + "4"
        override val description: String =
            "Error computing value, data in source '${source.get}' and path '${path.text}' is missing."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get,
            DETAILS_KEY_PATH to path.text
        )
    }

    class EvaluatingFeelExpression(cause: FeelExpression.EvaluateError) : ValueComputeErrors {
        override val code: String = PREFIX + "5"
        override val description: String = "Error evaluating a FEEL expression of computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class DataStructBuilding(cause: DataBuildErrors) : ValueComputeErrors {
        override val code: String = PREFIX + "6"
        override val description: String = "Error building a data struct of computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "VALUE-COMPUTE-"
        private const val DETAILS_KEY_PATH = "json-path"
        private const val DETAILS_KEY_SOURCE = "source-name"
        private const val DETAILS_KEY_ENV_VAR = "env-var-name"
    }
}

internal fun Value.computeOrNull(
    envVars: EnvVars,
    context: Context
): ResultK<JsonElement?, OptionalValueComputeErrors> =
    when (this) {
        is Value.Literal -> fact.asSuccess()

        is Value.Reference -> context[source]
            .mapFailure { failure ->
                OptionalValueComputeErrors.GettingDataFromContext(source = source, cause = failure)
            }
            .andThen { element ->
                element.search(path)
                    .mapFailure { failure ->
                        OptionalValueComputeErrors.SearchingDataByPath(path = path, cause = failure)
                    }
            }

        is Value.Expression -> expression.evaluate(envVars, context)
            .mapFailure { failure -> OptionalValueComputeErrors.EvaluatingFeelExpression(cause = failure) }

        is Value.EnvVars -> envVars[name]
            .mapFailure { failure -> OptionalValueComputeErrors.GettingValueFromEnvVars(name = name, cause = failure) }

        is Value.DataStruct -> this.scheme.build(envVars, context)
            .mapFailure { failure -> OptionalValueComputeErrors.DataStructBuilding(cause = failure) }
    }

internal sealed interface OptionalValueComputeErrors : BasicRulesEngineError {

    class GettingDataFromContext(
        source: Source,
        cause: GetDataFromContextErrors.SourceMissing
    ) : OptionalValueComputeErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error getting data from context by source '${source.get}' of computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class GettingValueFromEnvVars(
        name: EnvVarName,
        cause: GetValueFromEnvVarsErrors.EnvVarMissing
    ) : OptionalValueComputeErrors {
        override val code: String = PREFIX + "2"
        override val description: String =
            "Error getting value from environment variables by name '${name.get}' of computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_ENV_VAR to name.get
        )
    }

    class SearchingDataByPath(path: Path, cause: DataSearchError) : OptionalValueComputeErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error searching data by path '${path.text}' of computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_PATH to path.text
        )
    }

    class EvaluatingFeelExpression(cause: FeelExpression.EvaluateError) : OptionalValueComputeErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Error evaluating an expression of computing optional value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class DataStructBuilding(cause: DataBuildErrors) : OptionalValueComputeErrors {
        override val code: String = PREFIX + "5"
        override val description: String = "Error building a data struct of computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "OPTIONAL-VALUE-COMPUTE-"
        private const val DETAILS_KEY_PATH = "json-path"
        private const val DETAILS_KEY_SOURCE = "source-name"
        private const val DETAILS_KEY_ENV_VAR = "env-var-name"
    }
}
