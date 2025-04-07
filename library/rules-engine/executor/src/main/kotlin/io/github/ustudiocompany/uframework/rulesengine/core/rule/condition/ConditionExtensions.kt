package io.github.ustudiocompany.uframework.rulesengine.core.rule.condition

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.operation.CalculateOperationErrors
import io.github.ustudiocompany.uframework.rulesengine.core.operation.calculate

internal fun Condition?.isSatisfied(context: Context): ResultK<Boolean, CheckingConditionSatisfactionErrors> =
    if (this != null) isSatisfied(context) else Success.asTrue

private fun Condition.isSatisfied(context: Context): ResultK<Boolean, CheckingConditionSatisfactionErrors> {
    val isAllSatisfied = all { predicate ->
        predicate.isSatisfied(context)
            .getOrForward {
                return CheckingConditionSatisfactionErrors(it.cause).asFailure()
            }
    }
    return if (isAllSatisfied) Success.asTrue else Success.asFalse
}

internal class CheckingConditionSatisfactionErrors(cause: CheckingPredicateSatisfactionErrors) : BasicRulesEngineError {
    override val code: String = PREFIX + "1"
    override val description: String = "Checking condition satisfaction error."
    override val cause: Failure.Cause = Failure.Cause.Failure(cause)

    private companion object {
        private const val PREFIX = "CHECK-CONDITION-SATISFACTION-"
    }
}

private fun Predicate.isSatisfied(context: Context): ResultK<Boolean, CheckingPredicateSatisfactionErrors> =
    this.calculate(context)
        .mapFailure { failure -> CheckingPredicateSatisfactionErrors(failure) }

internal class CheckingPredicateSatisfactionErrors(cause: CalculateOperationErrors) : BasicRulesEngineError {
    override val code: String = PREFIX + "1"
    override val description: String = "Checking predicate satisfaction error."
    override val cause: Failure.Cause = Failure.Cause.Failure(cause)

    private companion object {
        private const val PREFIX = "CHECK-PREDICATE-SATISFACTION-"
    }
}
