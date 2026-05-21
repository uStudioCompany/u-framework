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
import io.github.ustudiocompany.uframework.rulesengine.core.context.UpdateContextErrors
import io.github.ustudiocompany.uframework.rulesengine.core.context.update
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputeErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.CheckingConditionSatisfactionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.CallProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger

internal fun HttpCallStep.executeIfSatisfied(
    envVars: EnvVars,
    context: Context,
    callProvider: CallProvider,
    merger: Merger
): Maybe<HttpCallStepExecuteErrors> {
    val step = this
    return maybeFailure {
        val (isSatisfied) = step.checkCondition(envVars, context)
        if (isSatisfied) {
            val uri = CallProvider.Uri.from(step.uri.get)
            val (args) = step.buildArgs(envVars, context)
            val (body) = step.buildBody(envVars, context)
            val (value) = callProvider.call(uri, args, body)
                .mapFailure { failure -> HttpCallStepExecuteErrors.Call(failure) }
            context.update(value, step.result, merger)
        } else
            Maybe.none()
    }
}

private fun Step.checkCondition(envVars: EnvVars, context: Context) =
    condition.isSatisfied(envVars, context)
        .mapFailure { failure -> HttpCallStepExecuteErrors.CheckingConditionSatisfaction(failure) }

private fun HttpCallStep.buildArgs(envVars: EnvVars, context: Context) =
    args.build(envVars, context) { name, value -> CallProvider.Arg(name, value) }
        .mapFailure { failure -> HttpCallStepExecuteErrors.ArgsBuilding(failure) }

private fun HttpCallStep.buildBody(envVars: EnvVars, context: Context) =
    body?.compute(envVars, context)
        ?.map2(
            onSuccess = { value -> CallProvider.Body(value) },
            onFailure = { failure -> HttpCallStepExecuteErrors.BodyBuilding(failure) }
        )
        ?: ResultK.Success.asNull

private fun Context.update(value: JsonElement, result: StepResult?, merger: Merger) =
    result?.let { result ->
        update(result.source, result.action, value, merger)
            .map { failure -> HttpCallStepExecuteErrors.UpdatingContext(failure) }
    } ?: Maybe.none()

internal sealed interface HttpCallStepExecuteErrors : BasicRulesEngineError {

    class CheckingConditionSatisfaction(cause: CheckingConditionSatisfactionErrors) : HttpCallStepExecuteErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error checking condition satisfaction of 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ArgsBuilding(cause: ArgsBuilderErrors) : HttpCallStepExecuteErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error building call args of 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class BodyBuilding(cause: ValueComputeErrors) : HttpCallStepExecuteErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error building call body of 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class Call(cause: CallProvider.Error) : HttpCallStepExecuteErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Error HTTP calling the 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class UpdatingContext(cause: UpdateContextErrors) : HttpCallStepExecuteErrors {
        override val code: String = PREFIX + "5"
        override val description: String = "Error updating context of 'HTTP Call' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "HTTP-CALL-STEP-EXECUTION-"
    }
}
