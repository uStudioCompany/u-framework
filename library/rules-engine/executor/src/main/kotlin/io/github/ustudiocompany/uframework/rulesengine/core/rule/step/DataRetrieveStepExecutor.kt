package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.context.ContextUpdateErrors
import io.github.ustudiocompany.uframework.rulesengine.core.context.update
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger

internal fun DataRetrieveStep.execute(
    envVars: EnvVars,
    context: Context,
    dataProvider: DataProvider,
    merger: Merger
): Maybe<DataRetrieveStepExecutionErrors> {
    val step = this
    return maybeFailure {
        val (args) = step.buildArgs(envVars, context)
        val uri = DataProvider.Uri.from(step.uri.get)
        val (value) = dataProvider.get(uri, args)
            .mapFailure { failure -> DataRetrieveStepExecutionErrors.ExternalDataRetrieval(failure) }
        context.update(value, step.result, merger)
    }
}

private fun DataRetrieveStep.buildArgs(envVars: EnvVars, context: Context) =
    args.build(envVars, context) { name, value -> DataProvider.Arg(name, value) }
        .mapFailure { failure -> DataRetrieveStepExecutionErrors.ArgBuild(failure) }

private fun Context.update(value: JsonElement, result: StepResult, merger: Merger) =
    update(result.source, result.action, value, merger)
        .map { failure -> DataRetrieveStepExecutionErrors.ContextUpdate(failure) }

internal sealed interface DataRetrieveStepExecutionErrors : BasicRulesEngineError {

    class ArgBuild(cause: ArgBuildErrors) : DataRetrieveStepExecutionErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error building args in the 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ExternalDataRetrieval(cause: DataProvider.Error) : DataRetrieveStepExecutionErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error retrieving external data in the 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ContextUpdate(cause: ContextUpdateErrors) : DataRetrieveStepExecutionErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error updating context in the 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-BUILD-STEP-EXECUTION-"
    }
}
