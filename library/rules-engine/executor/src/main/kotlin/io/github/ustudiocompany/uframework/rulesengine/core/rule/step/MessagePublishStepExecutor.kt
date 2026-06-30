package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.fail.asError
import io.github.airflux.commons.types.maybe.MaybeBiFailure
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.map2
import io.github.airflux.commons.types.resultk.mapFailureToError
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineIncident
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.toStringValue
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputationErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.executor.MessagePublisher

internal fun MessagePublishStep.execute(
    envVars: EnvVars,
    context: Context,
    messagePublisher: MessagePublisher
): MaybeBiFailure<MessagePublishStepExecutionErrors, MessagePublishStepExecutionIncident> {
    val step = this
    return maybeFailure {
        val (routeKey) = step.buildRouteKey(envVars, context)
        val (headers) = step.buildHeaders(envVars, context)
        val (body) = step.buildBody(envVars, context)
        messagePublisher.publish(routeKey, headers, body)
            .map(
                onError = { error -> MessagePublishStepExecutionErrors.Publish(error) },
                onException = { incident -> MessagePublishStepExecutionIncident.Publish(cause = incident) }
            )
    }
}

private fun MessagePublishStep.buildRouteKey(envVars: EnvVars, context: Context) =
    routeKey?.compute(envVars, context)
        ?.map2(
            onSuccess = { value -> value.toStringValue() },
            onFailure = { error -> MessagePublishStepExecutionErrors.RouteKeyBuild(error).asError() }
        )
        ?: ResultK.Success.asNull

private fun MessagePublishStep.buildHeaders(envVars: EnvVars, context: Context) =
    headers.build(envVars, context) { name, value -> MessagePublisher.Header(name, value) }
        .mapFailureToError { failure -> MessagePublishStepExecutionErrors.HeadersBuild(cause = failure) }

private fun MessagePublishStep.buildBody(envVars: EnvVars, context: Context) =
    body?.compute(envVars, context)
        ?.map2(
            onSuccess = { value -> value.toStringValue() },
            onFailure = { failure -> MessagePublishStepExecutionErrors.BodyBuild(cause = failure).asError() }
        )
        ?: ResultK.Success.asNull

internal sealed interface MessagePublishStepExecutionErrors : BasicRulesEngineError {

    class RouteKeyBuild(cause: ValueComputationErrors) : MessagePublishStepExecutionErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error building the route key for the message in the 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class HeadersBuild(cause: ArgBuildErrors) : MessagePublishStepExecutionErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error building headers for the message in the 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class BodyBuild(cause: ValueComputationErrors) : MessagePublishStepExecutionErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error building the body for the message in the 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class Publish(cause: MessagePublisher.Error) : MessagePublishStepExecutionErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Error publishing message in the 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "MESSAGE-PUBLISH-STEP-EXECUTION-ERROR-"
    }
}

internal sealed interface MessagePublishStepExecutionIncident : BasicRulesEngineIncident {

    class Publish(cause: MessagePublisher.Incident) : MessagePublishStepExecutionIncident {
        override val code: String = PREFIX + "1"
        override val description: String = "Error publishing message in the 'Message Publish' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "MESSAGE-PUBLISH-STEP-EXECUTION-INCIDENT-"
    }
}
