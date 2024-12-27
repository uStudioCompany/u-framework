package io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun Predicates?.isSatisfied(context: Context): ResultK<Boolean, RuleEngineError> =
    if (this != null) isSatisfied(context) else Success.asTrue

private fun Predicates.isSatisfied(context: Context): ResultK<Boolean, RuleEngineError> {
    val isAllSatisfied = all { predicate ->
        val isSatisfied = predicate.isSatisfied(context).getOrForward { return it }
        isSatisfied == true
    }
    return if (isAllSatisfied) Success.asTrue else Success.asFalse
}

private fun Predicate.isSatisfied(context: Context): ResultK<Boolean, RuleEngineError> = result {
    val (target) = target.compute(context)
    val (compareWith) = compareWith.compute(context)
    comparator.compare(target, compareWith)
}
