package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.asError
import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.mapFail
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.map2
import io.github.airflux.commons.types.resultk.mapFail
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineIncident
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.context.ContextErrors
import io.github.ustudiocompany.uframework.rulesengine.core.context.ContextIncident
import io.github.ustudiocompany.uframework.rulesengine.core.context.update
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputationErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.executor.HttpCallProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger

internal fun HttpCallStep.execute(
    envVars: EnvVars,
    context: Context,
    httpCallProvider: HttpCallProvider,
    merger: Merger
): Maybe<Fail<HttpCallStepExecutionErrors, HttpCallStepExecutionIncident>> {
    val step = this
    return maybeFailure {
        val uri = HttpCallProvider.Uri.from(step.uri.get)
        val (args) = step.buildArgs(envVars, context)
        val (body) = step.buildBody(envVars, context)
        val (value) = httpCallProvider.call(uri, args, body)
            .mapFail(
                onError = { error -> HttpCallStepExecutionErrors.Call(error) },
                onException = { incident -> HttpCallStepExecutionIncident.Call(incident) }
            )
        context.update(value, step.result, merger)
    }
}

private fun HttpCallStep.buildArgs(envVars: EnvVars, context: Context) =
    args.build(envVars, context) { name, value -> HttpCallProvider.Arg(name, value) }
        .mapFailure { failure -> HttpCallStepExecutionErrors.ArgBuild(failure).asError() }

private fun HttpCallStep.buildBody(envVars: EnvVars, context: Context) =
    body?.compute(envVars, context)
        ?.map2(
            onSuccess = { value -> HttpCallProvider.Body(value) },
            onFailure = { error -> HttpCallStepExecutionErrors.BodyBuild(error).asError() }
        )
        ?: ResultK.Success.asNull

private fun Context.update(
    value: JsonElement?,
    result: StepResult?,
    merger: Merger
): Maybe<Fail<HttpCallStepExecutionErrors, HttpCallStepExecutionIncident>> =
    when {
        result != null && value != null -> update(result.source, result.action, value, merger)
            .mapFail(
                onError = { error -> HttpCallStepExecutionErrors.ResultApply(error) },
                onException = { incident -> HttpCallStepExecutionIncident.ResultApply(incident) }
            )

        result != null && value == null -> Maybe.some(HttpCallStepExecutionErrors.ExpectedResponseMissing().asError())
        result == null && value != null -> Maybe.none()
        else -> Maybe.none()
    }

internal sealed interface HttpCallStepExecutionErrors : BasicRulesEngineError {

    class ArgBuild(cause: ArgBuildErrors) : HttpCallStepExecutionErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error building args in the 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class BodyBuild(cause: ValueComputationErrors) : HttpCallStepExecutionErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error building call body in the 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class Call(cause: HttpCallProvider.Error) : HttpCallStepExecutionErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error HTTP calling the 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ExpectedResponseMissing : HttpCallStepExecutionErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Expected response is missing in the 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.None
    }

    class ResultApply(cause: ContextErrors) : HttpCallStepExecutionErrors {
        override val code: String = PREFIX + "5"
        override val description: String = "Error of processing the result of the 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "HTTP-CALL-STEP-EXECUTION-ERROR-"
    }
}

internal sealed interface HttpCallStepExecutionIncident : BasicRulesEngineIncident {

    class Call(cause: HttpCallProvider.Incident) : HttpCallStepExecutionIncident {
        override val code: String = PREFIX + "1"
        override val description: String = "Incident HTTP calling the 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ResultApply(cause: ContextIncident) : HttpCallStepExecutionIncident {
        override val code: String = PREFIX + "2"
        override val description: String = "Incident of processing the result of the 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "HTTP-CALL-STEP-EXECUTION-INCIDENT-"
    }
}
