package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.toResultAsFailureOr
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataRetrieveStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.executeIfSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RulesEngineExecutorError

public typealias ExecutionResult = ResultK<ValidationStep.ErrorCode?, RulesEngineExecutorError>

public class RulesEngineExecutor(
    private val dataProvider: DataProvider,
    private val merger: Merger
) {

    public fun execute(envVars: EnvVars, context: Context, rules: Rules): ExecutionResult =
        rules.execute(envVars, context)

    private fun Rules.execute(envVars: EnvVars, context: Context): ExecutionResult {
        for (rule in this.get) {
            val result = rule.executeIfSatisfied(envVars, context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Rule.executeIfSatisfied(envVars: EnvVars, context: Context): ExecutionResult =
        condition.isSatisfied(envVars, context)
            .mapFailure { failure ->
                RulesEngineExecutorError.CheckingConditionSatisfactionRule(failure)
            }
            .flatMapBoolean(
                ifTrue = { this.steps.execute(envVars, context) },
                ifFalse = { Success.asNull }
            )

    private fun Steps.execute(envVars: EnvVars, context: Context): ExecutionResult {
        for (step in get) {
            val result = when (step) {
                is DataRetrieveStep -> step.execute(envVars, context)
                is DataBuildStep -> step.execute(envVars, context)
                is ValidationStep -> step.execute(envVars, context)
            }

            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun DataRetrieveStep.execute(envVars: EnvVars, context: Context): ExecutionResult =
        executeIfSatisfied(envVars, context, dataProvider, merger)
            .map { failure -> RulesEngineExecutorError.DataRetrievingStepExecute(failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun DataBuildStep.execute(envVars: EnvVars, context: Context): ExecutionResult =
        executeIfSatisfied(envVars, context, merger)
            .map { failure -> RulesEngineExecutorError.DataBuildStepExecute(failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun ValidationStep.execute(envVars: EnvVars, context: Context): ExecutionResult =
        executeIfSatisfied(envVars, context)
            .mapFailure { failure -> RulesEngineExecutorError.ValidationStepExecute(failure) }
}
