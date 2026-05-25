package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.executor.DataChangeTrackerProvider

internal fun DataChangeTrackingStep.execute(
    envVars: EnvVars,
    context: Context,
    dataChangeTrackerProvider: DataChangeTrackerProvider,
): Maybe<DataChangeTrackingStepExecutionErrors> {
    val step = this
    return maybeFailure {
        val (args) = step.buildArgs(envVars, context)
        val uri = DataChangeTrackerProvider.Uiss.from(step.uri.get)
        dataChangeTrackerProvider.prepare(uri, args)
            .map { failure -> DataChangeTrackingStepExecutionErrors.Preparing(failure) }
    }
}

private fun DataChangeTrackingStep.buildArgs(envVars: EnvVars, context: Context) =
    args.build(envVars, context) { name, value -> DataChangeTrackerProvider.Arg(name, value) }
        .mapFailure { failure -> DataChangeTrackingStepExecutionErrors.ArgBuild(failure) }

internal sealed interface DataChangeTrackingStepExecutionErrors : BasicRulesEngineError {

    class ArgBuild(cause: ArgBuildErrors) : DataChangeTrackingStepExecutionErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error building args in the 'Data Change Tracking' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class Preparing(cause: DataChangeTrackerProvider.Error) : DataChangeTrackingStepExecutionErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error preparing to track data changes."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-CHANGE-TRACKING-STEP-EXECUTION-"
    }
}
