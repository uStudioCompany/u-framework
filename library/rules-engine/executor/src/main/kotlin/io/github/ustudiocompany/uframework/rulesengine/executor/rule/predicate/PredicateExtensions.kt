package io.github.ustudiocompany.uframework.rulesengine.executor.rule.predicate

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.Predicates
import io.github.ustudiocompany.uframework.rulesengine.executor.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun Predicates?.isSatisfied(context: Context): ResultK<Boolean, RuleEngineError> =
    this?.isSatisfied(context) ?: Success.asTrue

private fun Predicates.isSatisfied(context: Context): ResultK<Boolean, RuleEngineError> =
    fold(true) { acc, predicate ->
        acc && predicate.isSatisfied(context).getOrForward { return it }
    }.asSuccess()

private fun Predicate.isSatisfied(context: Context): ResultK<Boolean, RuleEngineError> = result {
    val (target) = target.compute(context)
    val (compareWith) = compareWith.compute(context)
    comparator.compare(target, compareWith)
}
