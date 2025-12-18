package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.toResultAsFailureOr
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.env.append
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataChangeTrackingStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataRetrieveStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessagePublishStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.executeIfSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RulesEngineExecutorError

public typealias ExecutionResult = ResultK<ValidationStep.ErrorCode?, RulesEngineExecutorError>

public class RulesEngineExecutor(
    private val dataProvider: DataProvider,
    private val messagePublisher: MessagePublisher,
    private val dataChangeTrackerProvider: DataChangeTrackerProvider,
    private val merger: Merger
) {

    public fun execute(envVars: EnvVars, context: Context, rules: Rules): ExecutionResult =
        rules.execute(envVars, context)

    private fun Rules.execute(envVars: EnvVars, context: Context): ExecutionResult {
        for (rule in this.get) {
            val vars = envVars.append(RULE_ID to JsonElement.Text(rule.id.get))
            val result = rule.executeIfSatisfied(vars, context)
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
            val vars = envVars.append(STEP_ID to JsonElement.Text(step.id.get))
            val result = when (step) {
                is DataRetrieveStep -> step.execute(vars, context)
                is DataBuildStep -> step.execute(vars, context)
                is ValidationStep -> step.execute(vars, context)
                is MessagePublishStep -> step.execute(vars, context)
                is DataChangeTrackingStep -> step.execute(vars, context)
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

    private fun MessagePublishStep.execute(envVars: EnvVars, context: Context): ExecutionResult =
        executeIfSatisfied(envVars, context, messagePublisher)
            .map { failure -> RulesEngineExecutorError.MessagePublishStepExecute(failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun DataChangeTrackingStep.execute(envVars: EnvVars, context: Context): ExecutionResult =
        executeIfSatisfied(envVars, context, dataChangeTrackerProvider)
            .map { failure -> RulesEngineExecutorError.DataChangeTrackingStepExecute(failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private companion object {
        private val RULE_ID = EnvVarName("__RULE_ID__")
        private val STEP_ID = EnvVarName("__STEP_ID__")
    }
}
