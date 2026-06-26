package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.asError
import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.mapFail
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.mapFail
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineIncident
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.context.ContextUpdateErrors
import io.github.ustudiocompany.uframework.rulesengine.core.context.ContextUpdateIncident
import io.github.ustudiocompany.uframework.rulesengine.core.context.update
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger

internal fun DataRetrieveStep.execute(
    envVars: EnvVars,
    context: Context,
    dataProvider: DataProvider,
    merger: Merger
): Maybe<Fail<DataRetrieveStepExecutionErrors, DataRetrieveStepExecutionIncident>> {
    val step = this
    return maybeFailure {
        val (args: List<DataProvider.Arg>) = step.buildArgs(envVars, context)
        val uri: DataProvider.Uri = DataProvider.Uri.from(step.uri.get)
        val (value) = dataProvider.get(uri, args)
            .mapFail(
                onError = { error -> DataRetrieveStepExecutionErrors.ExternalDataRetrieval(error) },
                onException = { incident -> DataRetrieveStepExecutionIncident.ExternalDataRetrieval(incident) }
            )
        context.update(value, step.result, merger)
    }
}

private fun DataRetrieveStep.buildArgs(envVars: EnvVars, context: Context) =
    args.build(envVars, context) { name, value -> DataProvider.Arg(name, value) }
        .mapFailure { error -> DataRetrieveStepExecutionErrors.ArgBuild(error).asError() }

private fun Context.update(value: JsonElement, result: StepResult, merger: Merger) =
    update(result.source, result.action, value, merger)
        .mapFail(
            onError = { error -> DataRetrieveStepExecutionErrors.ResultApply(error) },
            onException = { incident -> DataRetrieveStepExecutionIncident.ResultApply(incident) }
        )

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

    class ResultApply(cause: ContextUpdateErrors) : DataRetrieveStepExecutionErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error of processing the result of the 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-BUILD-STEP-EXECUTION-ERROR-"
    }
}

internal sealed interface DataRetrieveStepExecutionIncident : BasicRulesEngineIncident {

    class ExternalDataRetrieval(cause: DataProvider.Incident) : DataRetrieveStepExecutionIncident {
        override val code: String = PREFIX + "1"
        override val description: String = "Incident retrieving external data in the 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ResultApply(cause: ContextUpdateIncident) : DataRetrieveStepExecutionIncident {
        override val code: String = PREFIX + "2"
        override val description: String = "Incident of processing the result of the 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-BUILD-STEP-EXECUTION-INCIDENT-"
    }
}
