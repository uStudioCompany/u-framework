package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.toStringValue
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputeErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.CheckingConditionSatisfactionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.MessagePublisher

internal fun MessagePublishStep.executeIfSatisfied(
    envVars: EnvVars,
    context: Context,
    messagePublisher: MessagePublisher
): Maybe<MessagePublishStepExecuteErrors> {
    val step = this
    return maybeFailure {
        val (isSatisfied) = step.condition.isSatisfied(envVars, context)
            .mapFailure { failure -> MessagePublishStepExecuteErrors.CheckingConditionSatisfaction(failure) }

        if (isSatisfied) {
            val routeKey = step.routeKey?.compute(envVars, context)
                ?.mapFailure { failure -> MessagePublishStepExecuteErrors.RouteKeyBuilding(failure) }
                ?.bind()
                ?.toStringValue()
            val (headers) = step.headers
                .build(envVars, context) { name, value -> MessagePublisher.Header(name, value) }
                .mapFailure { failure -> MessagePublishStepExecuteErrors.HeadersBuilding(cause = failure) }
            val body = step.body?.compute(envVars, context)
                ?.mapFailure { failure -> MessagePublishStepExecuteErrors.BodyBuilding(failure) }
                ?.bind()
                ?.toStringValue()
            messagePublisher.publish(routeKey, headers, body)
                .map { failure -> MessagePublishStepExecuteErrors.Publish(failure) }
        } else
            Maybe.none()
    }
}

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
