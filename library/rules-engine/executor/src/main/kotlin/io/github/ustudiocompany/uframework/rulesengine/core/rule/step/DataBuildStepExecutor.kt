package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.asError
import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.mapFail
import io.github.airflux.commons.types.maybe.maybeFailure
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
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger

internal fun DataBuildStep.execute(
    envVars: EnvVars,
    context: Context,
    merger: Merger
): Maybe<Fail<DataBuildStepExecutionErrors, DataBuildStepExecutionIncident>> {
    val step = this
    return maybeFailure {
        val (value) = step.buildData(envVars, context)
        context.update(value, step.result, merger)
    }
}

private fun DataBuildStep.buildData(envVars: EnvVars, context: Context) =
    dataSchema.build(envVars, context)
        .mapFailure { failure -> DataBuildStepExecutionErrors.DataBuild(failure).asError() }

private fun Context.update(value: JsonElement, result: StepResult?, merger: Merger) =
    result?.let { result ->
        update(result.source, result.action, value, merger)
            .mapFail(
                onError = { error -> DataBuildStepExecutionErrors.ResultApply(error) },
                onException = { incident -> DataBuildStepExecutionIncident.ResultApply(incident) }
            )
    } ?: Maybe.none()

internal sealed interface DataBuildStepExecutionErrors : BasicRulesEngineError {

    class DataBuild(cause: DataBuildErrors) : DataBuildStepExecutionErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error building data in the 'Data Build' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ResultApply(cause: ContextUpdateErrors) : DataBuildStepExecutionErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error of processing the result of the 'Data Build' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-BUILD-STEP-EXECUTION-ERROR-"
    }
}

internal sealed interface DataBuildStepExecutionIncident : BasicRulesEngineIncident {

    class ResultApply(cause: ContextUpdateIncident) : DataBuildStepExecutionIncident {
        override val code: String = PREFIX + "1"
        override val description: String = "Incident of processing the result of the 'Data Build' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-BUILD-STEP-EXECUTION-INCIDENT-"
    }
}
