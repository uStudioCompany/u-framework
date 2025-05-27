package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.context.UpdateContextErrors
import io.github.ustudiocompany.uframework.rulesengine.core.context.update
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.CheckingConditionSatisfactionErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger

internal fun DataBuildStep.executeIfSatisfied(
    envVars: EnvVars,
    context: Context,
    merger: Merger
): Maybe<DataBuildStepExecuteError> {
    val step = this
    return maybeFailure {
        val (isSatisfied) = condition.isSatisfied(envVars, context)
            .mapFailure { failure -> DataBuildStepExecuteError.CheckingConditionSatisfaction(failure) }

        if (isSatisfied) {
            val (value) = dataSchema.build(envVars, context)
                .mapFailure { failure -> DataBuildStepExecuteError.DataBuilding(failure) }
            val source = step.result.source
            val action = step.result.action
            context.update(source, action, value, merger)
                .map { failure -> DataBuildStepExecuteError.UpdatingContext(failure) }
        } else
            Maybe.none()
    }
}

internal sealed interface DataBuildStepExecuteError : BasicRulesEngineError {

    class CheckingConditionSatisfaction(cause: CheckingConditionSatisfactionErrors) : DataBuildStepExecuteError {
        override val code: String = PREFIX + "1"
        override val description: String = "Error check condition satisfaction of 'Data Build' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class DataBuilding(cause: DataBuildErrors) : DataBuildStepExecuteError {
        override val code: String = PREFIX + "2"
        override val description: String = "Error building data of 'Data Build' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class UpdatingContext(cause: UpdateContextErrors) : DataBuildStepExecuteError {
        override val code: String = PREFIX + "3"
        override val description: String = "Error updating context of 'Data Build' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-BUILD-STEP-EXECUTION-"
    }
}
