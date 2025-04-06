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
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.execute
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RulesExecutionError

public typealias ExecutionResult = ResultK<ValidationStep.ErrorCode?, RulesExecutionError>

@Suppress("TooManyFunctions")
public class RulesEngineExecutor(
    private val dataProvider: DataProvider,
    private val merger: Merger
) {

    public fun execute(context: Context, rules: Rules): ExecutionResult = rules.execute(context)

    private fun Rules.execute(context: Context): ExecutionResult {
        for (rule in this) {
            val result = rule.executeIfSatisfied(context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Rule.executeIfSatisfied(context: Context): ExecutionResult =
        condition.isSatisfied(context)
            .mapFailure { failure ->
                RulesExecutionError.CheckingConditionSatisfactionRule(failure)
            }
            .flatMapBoolean(
                ifTrue = { this.steps.execute(context) },
                ifFalse = { Success.asNull }
            )

    private fun Steps.execute(context: Context): ExecutionResult {
        for (step in this) {
            val result = when (step) {
                is DataRetrieveStep ->
                    step.execute(context, dataProvider, merger)
                        .map { failure ->
                            RulesExecutionError.DataRetrievingStepExecute(failure)
                        }
                        .toResultAsFailureOr(ResultK.Success.asNull)

                is DataBuildStep ->
                    step.execute(context, merger)
                        .map { failure ->
                            RulesExecutionError.DataBuildStepExecute(failure)
                        }
                        .toResultAsFailureOr(ResultK.Success.asNull)

                is ValidationStep -> step.execute(context)
                    .mapFailure { failure ->
                        RulesExecutionError.ValidationStepExecute(failure)
                    }
            }

            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }
}
