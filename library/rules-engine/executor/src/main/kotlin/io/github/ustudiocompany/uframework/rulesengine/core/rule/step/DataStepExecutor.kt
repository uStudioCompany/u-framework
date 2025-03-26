package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.context.update
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun DataStep.execute(context: Context, merger: Merger): Maybe<RuleEngineError> {
    val step = this
    return maybeFailure {
        val (isSatisfied) = condition.isSatisfied(context)
        if (isSatisfied) {
            val (value) = dataScheme.build(context)
            val source = step.result.source
            val action = step.result.action
            context.update(source, action, value, merger::merge)
        } else
            Maybe.none()
    }
}
