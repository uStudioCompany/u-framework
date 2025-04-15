package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.maybe.map
import io.github.airflux.commons.types.maybe.toResultAsFailureOr
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
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

    public fun execute(mergeStrategyCode: MergeStrategyCode, context: Context, rules: Rules): ExecutionResult =
        rules.execute(mergeStrategyCode, context)

    private fun Rules.execute(mergeStrategyCode: MergeStrategyCode, context: Context): ExecutionResult {
        for (rule in this.get) {
            val result = rule.executeIfSatisfied(mergeStrategyCode, context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Rule.executeIfSatisfied(mergeStrategyCode: MergeStrategyCode, context: Context): ExecutionResult =
        condition.isSatisfied(context)
            .mapFailure { failure ->
                RulesEngineExecutorError.CheckingConditionSatisfactionRule(failure)
            }
            .flatMapBoolean(
                ifTrue = { this.steps.execute(mergeStrategyCode, context) },
                ifFalse = { Success.asNull }
            )

    private fun Steps.execute(mergeStrategyCode: MergeStrategyCode, context: Context): ExecutionResult {
        for (step in get) {
            val result = when (step) {
                is DataRetrieveStep -> step.execute(mergeStrategyCode, context)
                is DataBuildStep -> step.execute(mergeStrategyCode, context)
                is ValidationStep -> step.execute(context)
            }

            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun DataRetrieveStep.execute(mergeStrategyCode: MergeStrategyCode, context: Context): ExecutionResult =
        executeIfSatisfied(mergeStrategyCode, context, dataProvider, merger)
            .map { failure -> RulesEngineExecutorError.DataRetrievingStepExecute(failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun DataBuildStep.execute(mergeStrategyCode: MergeStrategyCode, context: Context): ExecutionResult =
        executeIfSatisfied(mergeStrategyCode, context, merger)
            .map { failure -> RulesEngineExecutorError.DataBuildStepExecute(failure) }
            .toResultAsFailureOr(ResultK.Success.asNull)

    private fun ValidationStep.execute(context: Context): ExecutionResult =
        executeIfSatisfied(context)
            .mapFailure { failure -> RulesEngineExecutorError.ValidationStepExecute(failure) }
}
