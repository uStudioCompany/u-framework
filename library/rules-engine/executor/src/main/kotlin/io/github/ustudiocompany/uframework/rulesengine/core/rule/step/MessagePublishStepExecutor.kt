package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.map2
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.toStringValue
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputeErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.CheckingConditionSatisfactionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.Condition
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.MessagePublisher

internal fun MessagePublishStep.executeIfSatisfied(
    envVars: EnvVars,
    context: Context,
    messagePublisher: MessagePublisher
): Maybe<MessagePublishStepExecuteErrors> {
    val step = this
    return maybeFailure {
        val (isSatisfied) = checkCondition(step.condition, envVars, context)
        if (isSatisfied) {
            val (routeKey) = buildRouteKey(step.routeKey, envVars, context)
            val (headers) = buildHeaders(step.headers, envVars, context)
            val (body) = buildBody(step.body, envVars, context)
            messagePublisher.publish(routeKey, headers, body)
                .map { failure -> MessagePublishStepExecuteErrors.Publish(failure) }
        } else
            Maybe.none()
    }
}

private fun checkCondition(condition: Condition, envVars: EnvVars, context: Context) =
    condition.isSatisfied(envVars, context)
        .mapFailure { failure -> MessagePublishStepExecuteErrors.CheckingConditionSatisfaction(failure) }

private fun buildRouteKey(routeKey: Value?, envVars: EnvVars, context: Context) =
    routeKey?.compute(envVars, context)
        ?.map2(
            onSuccess = { value -> value.toStringValue() },
            onFailure = { failure -> MessagePublishStepExecuteErrors.RouteKeyBuilding(failure) }
        )
        ?: ResultK.Success.asNull

private fun buildHeaders(headers: MessageHeaders, envVars: EnvVars, context: Context) =
    headers.build(envVars, context) { name, value -> MessagePublisher.Header(name, value) }
        .mapFailure { failure -> MessagePublishStepExecuteErrors.HeadersBuilding(cause = failure) }

private fun buildBody(body: Value?, envVars: EnvVars, context: Context) =
    body?.compute(envVars, context)
        ?.map2(
            onSuccess = { value -> value.toStringValue() },
            onFailure = { failure -> MessagePublishStepExecuteErrors.BodyBuilding(failure) }
        )
        ?: ResultK.Success.asNull

internal sealed interface MessagePublishStepExecuteErrors : BasicRulesEngineError {

    class CheckingConditionSatisfaction(cause: CheckingConditionSatisfactionErrors) : MessagePublishStepExecuteErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error checking condition satisfaction of 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class RouteKeyBuilding(cause: ValueComputeErrors) : MessagePublishStepExecuteErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error building the route key for the message of 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class HeadersBuilding(cause: ArgsBuilderErrors) : MessagePublishStepExecuteErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error building headers for the message of 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class BodyBuilding(cause: ValueComputeErrors) : MessagePublishStepExecuteErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Error building the body for the message of 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class Publish(cause: MessagePublisher.Error) : MessagePublishStepExecuteErrors {
        override val code: String = PREFIX + "5"
        override val description: String = "Error publishing message of 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "MESSAGE-PUBLISH-STEP-EXECUTION-"
    }
}
