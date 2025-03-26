package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.maybeFailure
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.core.rule.uri.build
import io.github.ustudiocompany.uframework.rulesengine.executor.CallProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.context.update
import io.github.ustudiocompany.uframework.rulesengine.executor.error.CallStepError
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun CallStep.execute(context: Context, callProvider: CallProvider, merger: Merger): Maybe<RuleEngineError> {
    val step = this
    return maybeFailure {
        val (isSatisfied) = condition.isSatisfied(context)
        if (isSatisfied) {
            val (request) = step.buildRequest(context)
            val (value) = callProvider.call(request).mapFailure { CallStepError(it) }
            val source = step.result.source
            val action = step.result.action
            context.update(source, action, value, merger::merge)
        } else
            Maybe.none()
    }
}

private fun CallStep.buildRequest(context: Context) = result {
    val (uri) = uri.build()
    val (args) = args.build(context)
    CallProvider.Request(uri, args)
}
