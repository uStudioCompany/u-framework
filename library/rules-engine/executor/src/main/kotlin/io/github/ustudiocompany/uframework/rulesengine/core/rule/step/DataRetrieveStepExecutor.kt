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
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger

internal fun DataRetrieveStep.executeIfSatisfied(
    envVars: EnvVars,
    context: Context,
    dataProvider: DataProvider,
    merger: Merger
): Maybe<DataRetrieveStepExecuteErrors> {
    val step = this
    return maybeFailure {
        val (isSatisfied) = condition.isSatisfied(envVars, context)
            .mapFailure { failure -> DataRetrieveStepExecuteErrors.CheckingConditionSatisfaction(failure) }

        if (isSatisfied) {
            val (args) = args.build(envVars, context)
                .mapFailure { failure -> DataRetrieveStepExecuteErrors.ArgsBuilding(failure) }
            val uri = DataProvider.Uri.from(step.uri.get)
            val (value) = dataProvider.get(uri, args)
                .mapFailure { failure -> DataRetrieveStepExecuteErrors.RetrievingExternalData(failure) }
            val source = step.result.source
            val action = step.result.action
            context.update(source, action, value, merger)
                .map { failure -> DataRetrieveStepExecuteErrors.UpdatingContext(failure) }
        } else
            Maybe.none()
    }
}

internal sealed interface DataRetrieveStepExecuteErrors : BasicRulesEngineError {

    class CheckingConditionSatisfaction(cause: CheckingConditionSatisfactionErrors) : DataRetrieveStepExecuteErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error checking condition satisfaction of 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class ArgsBuilding(cause: DataProviderArgsErrors) : DataRetrieveStepExecuteErrors {
        override val code: String = PREFIX + "2"
        override val description: String = "Error building args for data provider of 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class RetrievingExternalData(cause: DataProvider.Error) : DataRetrieveStepExecuteErrors {
        override val code: String = PREFIX + "3"
        override val description: String = "Error retrieving external data of 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    class UpdatingContext(cause: UpdateContextErrors) : DataRetrieveStepExecuteErrors {
        override val code: String = PREFIX + "4"
        override val description: String = "Error updating context of 'Data Retrieve' step."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-BUILD-STEP-EXECUTION-"
    }
}
