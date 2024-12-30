package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.update
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult
import io.github.ustudiocompany.uframework.rulesengine.executor.Merger
import io.github.ustudiocompany.uframework.rulesengine.executor.build

internal fun DataStep.execute(context: Context, merger: Merger): ExecutionResult =
    condition.isSatisfied(context)
        .flatMapBoolean(
            ifTrue = {
                val step = this
                dataScheme.build(context)
                    .andThen { value ->
                        val source = step.result.source
                        val action = step.result.action
                        context.update(source, action, value, merger::merge)
                    }
            },
            ifFalse = { Success.asNull }
        )
