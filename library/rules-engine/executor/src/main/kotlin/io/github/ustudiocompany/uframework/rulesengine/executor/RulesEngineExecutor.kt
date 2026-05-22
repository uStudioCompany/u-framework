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
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.HttpCallStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessagePublishStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.execute
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleExecuteErrors
import io.github.ustudiocompany.uframework.rulesengine.executor.error.StepExecuteErrors

public typealias ExecutionResult = ResultK<ValidationStep.ErrorCode?, RuleExecuteErrors>

@Suppress("TooManyFunctions")
public class RulesEngineExecutor(
    private val callProvider: CallProvider,
    private val dataProvider: DataProvider,
    private val messagePublisher: MessagePublisher,
    private val dataChangeTrackerProvider: DataChangeTrackerProvider,
    private val merger: Merger
) {

    public fun execute(envVars: EnvVars, context: Context, rules: Rules): ExecutionResult =
        rules.execute(envVars, context)

    private fun Rules.execute(
        envVars: EnvVars,
        context: Context
    ): ResultK<ValidationStep.ErrorCode?, RuleExecuteErrors> {
        for (rule in get) {
            val result = rule.executeIfConditionSatisfied(envVars, context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Rule.executeIfConditionSatisfied(envVars: EnvVars, context: Context): ExecutionResult =
        checkCondition(envVars, context)
            .flatMapBoolean(
                ifTrue = {
                    val vars = envVars.append(RULE_ID to JsonElement.Text(id.get))
                    steps.execute(vars, context)
                        .mapFailure { failure -> RuleExecuteErrors.Execution(ruleId = id, cause = failure) }
                },
                ifFalse = { Success.asNull }
            )

    private fun Rule.checkCondition(envVars: EnvVars, context: Context) =
        condition.isSatisfied(envVars, context)
            .mapFailure { failure -> RuleExecuteErrors.CheckingConditionSatisfaction(ruleId = id, cause = failure) }

    private fun Steps.execute(
        envVars: EnvVars,
        context: Context
    ): ResultK<ValidationStep.ErrorCode?, StepExecuteErrors> {
        for (step in get) {
            val result = step.executeIfConditionSatisfied(envVars, context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Step.executeIfConditionSatisfied(envVars: EnvVars, context: Context) =
        checkCondition(envVars, context)
            .flatMapBoolean(
                ifTrue = {
                    val vars = envVars.append(STEP_ID to JsonElement.Text(id.get))
                    when (this) {
                        is DataRetrieveStep -> tryExecute(vars, context)
                        is DataBuildStep -> tryExecute(vars, context)
                        is ValidationStep -> tryExecute(vars, context)
                        is MessagePublishStep -> tryExecute(vars, context)
                        is DataChangeTrackingStep -> tryExecute(vars, context)
                        is HttpCallStep -> tryExecute(vars, context)
                    }
                },
                ifFalse = { Success.asNull }
            )

    private fun Step.checkCondition(envVars: EnvVars, context: Context) =
        condition.isSatisfied(envVars, context)
            .mapFailure { failure -> StepExecuteErrors.CheckingConditionSatisfaction(stepId = id, cause = failure) }

    private fun DataRetrieveStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context, dataProvider, merger)
            .map { failure -> StepExecuteErrors.DataRetrieving(stepId = id, cause = failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun DataBuildStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context, merger)
            .map { failure -> StepExecuteErrors.DataBuilding(stepId = id, cause = failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun ValidationStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context)
            .mapFailure { failure -> StepExecuteErrors.Validation(stepId = id, cause = failure) }

    private fun MessagePublishStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context, messagePublisher)
            .map { failure -> StepExecuteErrors.MessagePublishing(stepId = id, cause = failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun DataChangeTrackingStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context, dataChangeTrackerProvider)
            .map { failure -> StepExecuteErrors.DataChangeTracking(stepId = id, cause = failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun HttpCallStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context, callProvider, merger)
            .map { failure -> StepExecuteErrors.HttpCalling(stepId = id, cause = failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private companion object {
        private val RULE_ID = EnvVarName("__RULE_ID__")
        private val STEP_ID = EnvVarName("__STEP_ID__")
    }
}
