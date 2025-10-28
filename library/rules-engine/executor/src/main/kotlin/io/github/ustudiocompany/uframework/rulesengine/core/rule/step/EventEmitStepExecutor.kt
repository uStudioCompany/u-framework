package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.CheckingConditionSatisfactionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.EventEmitter

internal fun EventEmitStep.executeIfSatisfied(
    envVars: EnvVars,
    context: Context,
    emitter: EventEmitter
): Maybe<EmitEventStepExecuteErrors> {
    val step = this
    return maybeFailure {
        val (isSatisfied) = step.condition.isSatisfied(envVars, context)
            .mapFailure { failure -> EmitEventStepExecuteErrors.CheckingConditionSatisfaction(failure) }

        if (isSatisfied) {
            val (args) = step.args
                .build(envVars, context) { name, value -> EventEmitter.Arg(name, value) }
                .mapFailure { failure -> EmitEventStepExecuteErrors.ArgsBuilding(cause = failure) }
            emitter.emit(args)
                .map { failure -> EmitEventStepExecuteErrors.Emit(failure) }
        } else
            Maybe.none()
    }
}

internal sealed interface EmitEventStepExecuteErrors : BasicRulesEngineError {

    class CheckingConditionSatisfaction(cause: CheckingConditionSatisfactionErrors) : EmitEventStepExecuteErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error checking condition satisfaction of 'Event Emit' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ArgsBuilding(cause: ArgsBuilderErrors) : EmitEventStepExecuteErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error building args for event emitter of 'Event Emit' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class Emit(cause: EventEmitter.Error) : EmitEventStepExecuteErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error emit event of 'Event Emit' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "EVENT-EMIT-STEP-EXECUTION-"
    }
}
