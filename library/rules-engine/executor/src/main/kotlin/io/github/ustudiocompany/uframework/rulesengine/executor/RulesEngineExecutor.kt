package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.toResultAsFailureOr
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.mapFailureToError
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarName
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.env.append
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataBuildStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataChangeTrackingStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.DataRetrieveStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.HttpCallStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.MessagePublishStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.ValidationStep
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.execute
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleExecutionErrors
import io.github.ustudiocompany.uframework.rulesengine.executor.error.StepExecutionErrors
import io.github.ustudiocompany.uframework.rulesengine.executor.incident.RuleExecutionIncident
import io.github.ustudiocompany.uframework.rulesengine.executor.incident.StepExecutionIncident
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.isApplicable
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.step.isApplicable

public typealias ExecutionResult = ResultK<ValidationStep.ErrorCode?, Fail<RuleExecutionErrors, RuleExecutionIncident>>

@Suppress("TooManyFunctions")
public class RulesEngineExecutor(
    private val httpCallProvider: HttpCallProvider,
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
    ): ResultK<ValidationStep.ErrorCode?, Fail<RuleExecutionErrors, RuleExecutionIncident>> {
        for (rule in get) {
            val result = rule.executeIfApplicable(envVars, context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Rule.executeIfApplicable(envVars: EnvVars, context: Context): ExecutionResult =
        isApplicable(envVars, context)
            .flatMapBoolean(
                ifTrue = {
                    val vars = envVars.append(RULE_ID to JsonElement.Text(id.get))
                    steps.execute(vars, context)
                        .mapFailure(
                            onError = { error -> RuleExecutionErrors.Execution(ruleId = id, cause = error) },
                            onException = { incident -> RuleExecutionIncident.Execution(ruleId = id, cause = incident) }
                        )
                },
                ifFalse = { Success.asNull }
            )

    private fun Steps.execute(
        envVars: EnvVars,
        context: Context
    ): ResultK<ValidationStep.ErrorCode?, Fail<StepExecutionErrors, StepExecutionIncident>> {
        for (step in get) {
            val result = step.executeIfApplicable(envVars, context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Step.executeIfApplicable(
        envVars: EnvVars,
        context: Context
    ): ResultK<ValidationStep.ErrorCode?, Fail<StepExecutionErrors, StepExecutionIncident>> =
        isApplicable(envVars, context)
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

    private fun DataRetrieveStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context, dataProvider, merger)
            .map(
                onError = { error -> StepExecutionErrors.DataRetrieve(stepId = id, cause = error) },
                onException = { incident -> StepExecutionIncident.DataRetrieve(stepId = id, cause = incident) }
            )
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun DataBuildStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context, merger)
            .map(
                onError = { error -> StepExecutionErrors.DataBuild(stepId = id, cause = error) },
                onException = { incident -> StepExecutionIncident.DataBuild(stepId = id, cause = incident) }
            )
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun ValidationStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context)
            .mapFailureToError { failure -> StepExecutionErrors.Validation(stepId = id, cause = failure) }

    private fun MessagePublishStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context, messagePublisher)
            .map(
                onError = { error -> StepExecutionErrors.MessagePublish(stepId = id, cause = error) },
                onException = { incident -> StepExecutionIncident.MessagePublish(stepId = id, cause = incident) }
            )
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun DataChangeTrackingStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context, dataChangeTrackerProvider)
            .map(
                onError = { error -> StepExecutionErrors.DataChangeTracking(stepId = id, cause = error) },
                onException = { incident -> StepExecutionIncident.DataChangeTracking(stepId = id, cause = incident) }
            )
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun HttpCallStep.tryExecute(envVars: EnvVars, context: Context) =
        execute(envVars, context, httpCallProvider, merger)
            .map(
                onError = { error -> StepExecutionErrors.HttpCall(stepId = id, cause = error) },
                onException = { incident -> StepExecutionIncident.HttpCall(stepId = id, cause = incident) }
            )
            .toResultAsFailureOr(ResultK.Success.asNull)

    private companion object {
        private val RULE_ID = EnvVarName("__RULE_ID__")
        private val STEP_ID = EnvVarName("__STEP_ID__")
    }
}
