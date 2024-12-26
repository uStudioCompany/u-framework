package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.isFailure
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rule
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Rules
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Step.ErrorCode
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.Steps
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.predicate.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.rule.step.execute

public typealias ExecutionResult = ResultK<Step.ErrorCode?, RuleEngineError>

@Suppress("TooManyFunctions")
public class RulesEngineExecutor(
    private val provider: DataProvider,
    private val merger: Merger
) {

    public fun execute(context: Context, rules: Rules): ExecutionResult = rules.execute(context)

    private fun Rules.execute(context: Context): ResultK<ErrorCode?, RuleEngineError> {
        for (rule in this) {
            val result = rule.executeIfSatisfied(context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Rule.executeIfSatisfied(context: Context): ResultK<ErrorCode?, RuleEngineError> =
        predicate.isSatisfied(context)
            .flatMapBoolean(
                ifTrue = { this.steps.execute(context) },
                ifFalse = { Success.asNull }
            )

    private fun Steps.execute(context: Context): ResultK<ErrorCode?, RuleEngineError> {
        for (step in this) {
            val result = step.executeIfSatisfied(context)
            if (result.isFailure() || result.value != null) return result
        }
        return Success.asNull
    }

    private fun Step.executeIfSatisfied(context: Context): ResultK<ErrorCode?, RuleEngineError> =
        predicate.isSatisfied(context)
            .flatMapBoolean(
                ifTrue = {
                    when (this) {
                        is Step.Call -> this.execute(context, provider, merger)
                        is Step.Requirement -> this.execute(context)
                        is Step.Action -> this.execute(context, merger)
                    }
                },
                ifFalse = { Success.asNull },
            )
}
