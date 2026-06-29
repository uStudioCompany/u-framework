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
import io.github.ustudiocompany.uframework.rulesengine.core.context.ContextErrors
import io.github.ustudiocompany.uframework.rulesengine.core.context.get
import io.github.ustudiocompany.uframework.rulesengine.core.data.search
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarReadingErrors
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.env.get
import io.github.ustudiocompany.uframework.rulesengine.core.feel.FeelExpression
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.build

internal fun Value.compute(envVars: EnvVars, context: Context): ResultK<JsonElement, ValueComputationErrors> =
    when (this) {
        is Value.Literal -> fact.asSuccess()

        is Value.Reference -> context[source]
            .mapFailure { failure -> ValueComputationErrors.ContextDataRetrieval(source = source, cause = failure) }
            .andThen { element ->
                element.search(path)
                    .mapFailure { failure -> ValueComputationErrors.PathSearch(path = path, cause = failure) }
                    .filterNotNull { ValueComputationErrors.DataNotFoundAtPath(source = source, path = path) }
            }

        is Value.Expression -> expression.evaluate(envVars, context)
            .mapFailure { failure -> ValueComputationErrors.FeelExpressionEvaluate(cause = failure) }

        is Value.EnvVars -> envVars[name]
            .mapFailure { failure -> ValueComputationErrors.EnvVarReading(name = name, cause = failure) }

        is Value.DataStruct -> this.scheme.build(envVars, context)
            .mapFailure { failure -> ValueComputationErrors.DataStructBuild(cause = failure) }
    }

internal sealed interface ValueComputationErrors : BasicRulesEngineError {

    class ContextDataRetrieval(source: Source, cause: ContextErrors.SourceMissing) : ValueComputationErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error retrieving a data from context by source '${source.get}' for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class EnvVarReading(
        name: EnvVarName,
        cause: EnvVarReadingErrors.EnvVarMissing
    ) : ValueComputationErrors {
        override val code: String = PREFIX + "2"
        override val description: String =
            "Error reading a value from environment variables by name '${name.get}' for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_ENV_VAR to name.get
        )
    }

    class PathSearch(path: Path, cause: Path.SearchError) : ValueComputationErrors {
        override val code: String = PREFIX + "3"
        override val description: String =
            "Error searching within data at the specified path '${path.text}' for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_PATH to path.text
        )
    }

    class DataNotFoundAtPath(source: Source, path: Path) : ValueComputationErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Data in source '${source.get}' by path '${path.text}' is missing."
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get,
            DETAILS_KEY_PATH to path.text
        )
    }

    class FeelExpressionEvaluate(cause: FeelExpression.EvaluateError) : ValueComputationErrors {
        override val code: String = PREFIX + "5"
        override val description: String = "Error evaluating a FEEL expression for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class DataStructBuild(cause: DataBuildErrors) : ValueComputationErrors {
        override val code: String = PREFIX + "6"
        override val description: String = "Error building a data struct for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "VALUE-COMPUTE-ERROR-"
        private const val DETAILS_KEY_PATH = "json-path"
        private const val DETAILS_KEY_SOURCE = "source-name"
        private const val DETAILS_KEY_ENV_VAR = "env-var-name"
    }
}

internal fun Value.computeOrNull(
    envVars: EnvVars,
    context: Context
): ResultK<JsonElement?, OptionalValueComputationErrors> =
    when (this) {
        is Value.Literal -> fact.asSuccess()

        is Value.Reference -> context[source]
            .mapFailure { error -> OptionalValueComputationErrors.ContextDataRetrieval(source = source, cause = error) }
            .andThen { element ->
                element.search(path)
                    .mapFailure { error -> OptionalValueComputationErrors.PathSearch(path = path, cause = error) }
            }

        is Value.Expression -> expression.evaluate(envVars, context)
            .mapFailure { failure -> OptionalValueComputationErrors.FeelExpressionEvaluate(cause = failure) }

        is Value.EnvVars -> envVars[name]
            .mapFailure { failure -> OptionalValueComputationErrors.EnvVarReading(name = name, cause = failure) }

        is Value.DataStruct -> this.scheme.build(envVars, context)
            .mapFailure { failure -> OptionalValueComputationErrors.DataStructBuild(cause = failure) }
    }

internal sealed interface OptionalValueComputationErrors : BasicRulesEngineError {

    class ContextDataRetrieval(
        source: Source,
        cause: ContextErrors.SourceMissing
    ) : OptionalValueComputationErrors {
        override val code: String = PREFIX + "1"
        override val description: String =
            "Error retrieving a data from context by source '${source.get}' for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_SOURCE to source.get
        )
    }

    class EnvVarReading(
        name: EnvVarName,
        cause: EnvVarReadingErrors.EnvVarMissing
    ) : OptionalValueComputationErrors {
        override val code: String = PREFIX + "2"
        override val description: String =
            "Error reading a value from environment variables by name '${name.get}' for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_ENV_VAR to name.get
        )
    }

    class PathSearch(path: Path, cause: Path.SearchError) : OptionalValueComputationErrors {
        override val code: String = PREFIX + "3"
        override val description: String =
            "Error searching within data at the specified path '${path.text}' for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
        override val details: Failure.Details = Failure.Details.of(
            DETAILS_KEY_PATH to path.text
        )
    }

    class FeelExpressionEvaluate(cause: FeelExpression.EvaluateError) : OptionalValueComputationErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Error evaluating a FEEL expression for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class DataStructBuild(cause: DataBuildErrors) : OptionalValueComputationErrors {
        override val code: String = PREFIX + "5"
        override val description: String = "Error building a data struct for computing value."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "OPTIONAL-VALUE-COMPUTE-ERROR-"
        private const val DETAILS_KEY_PATH = "json-path"
        private const val DETAILS_KEY_SOURCE = "source-name"
        private const val DETAILS_KEY_ENV_VAR = "env-var-name"
    }
}
