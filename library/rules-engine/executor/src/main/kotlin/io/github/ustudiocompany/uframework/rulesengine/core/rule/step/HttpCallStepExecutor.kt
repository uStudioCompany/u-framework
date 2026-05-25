package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.map2
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.context.ContextUpdateErrors
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
): Maybe<HttpCallStepExecutionErrors> {
    val step = this
    return maybeFailure {
        val uri = HttpCallProvider.Uri.from(step.uri.get)
        val (args) = step.buildArgs(envVars, context)
        val (body) = step.buildBody(envVars, context)
        val (value) = httpCallProvider.call(uri, args, body)
            .mapFailure { failure -> HttpCallStepExecutionErrors.Call(failure) }
        context.update(value, step.result, merger)
    }
}

private fun HttpCallStep.buildArgs(envVars: EnvVars, context: Context) =
    args.build(envVars, context) { name, value -> HttpCallProvider.Arg(name, value) }
        .mapFailure { failure -> HttpCallStepExecutionErrors.ArgBuild(failure) }

private fun HttpCallStep.buildBody(envVars: EnvVars, context: Context) =
    body?.compute(envVars, context)
        ?.map2(
            onSuccess = { value -> HttpCallProvider.Body(value) },
            onFailure = { failure -> HttpCallStepExecutionErrors.BodyBuild(failure) }
        )
        ?: ResultK.Success.asNull

private fun Context.update(value: JsonElement, result: StepResult?, merger: Merger) =
    result?.let { result ->
        update(result.source, result.action, value, merger)
            .map { failure -> HttpCallStepExecutionErrors.ContextUpdate(failure) }
    } ?: Maybe.none()

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

    class ContextUpdate(cause: ContextUpdateErrors) : HttpCallStepExecutionErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Error updating context in the 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "HTTP-CALL-STEP-EXECUTION-"
    }
}
