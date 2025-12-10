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
import io.github.ustudiocompany.uframework.rulesengine.executor.DataChangeTrackerProvider

internal fun DataChangeTrackingStep.executeIfSatisfied(
    envVars: EnvVars,
    context: Context,
    dataChangeTrackerProvider: DataChangeTrackerProvider,
): Maybe<DataChangeTrackingStepExecuteErrors> {
    val step = this
    return maybeFailure {
        val (isSatisfied) = condition.isSatisfied(envVars, context)
            .mapFailure { failure -> DataChangeTrackingStepExecuteErrors.CheckingConditionSatisfaction(failure) }

        if (isSatisfied) {
            val (args) = args.build(envVars, context) { name, value ->
                DataChangeTrackerProvider.Arg(name, value)
            }.mapFailure { failure -> DataChangeTrackingStepExecuteErrors.ArgsBuilding(failure) }
            val uri = DataChangeTrackerProvider.Uiss.from(step.uri.get)
            dataChangeTrackerProvider.prepare(uri, args)
                .map { failure -> DataChangeTrackingStepExecuteErrors.Preparing(failure) }
        } else
            Maybe.none()
    }
}

internal sealed interface DataChangeTrackingStepExecuteErrors : BasicRulesEngineError {

    class CheckingConditionSatisfaction(cause: CheckingConditionSatisfactionErrors) :
        DataChangeTrackingStepExecuteErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error checking condition satisfaction of 'Data Change Tracking' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ArgsBuilding(cause: ArgsBuilderErrors) : DataChangeTrackingStepExecuteErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error building args for data provider of 'Data Change Tracking' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class Preparing(cause: DataChangeTrackerProvider.Error) : DataChangeTrackingStepExecuteErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error preparing to track data changes."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-CHANGE-TRACKING-STEP-EXECUTION-"
    }
}
