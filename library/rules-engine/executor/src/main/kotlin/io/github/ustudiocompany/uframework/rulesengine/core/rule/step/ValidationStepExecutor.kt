package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult

internal fun ValidationStep.execute(context: Context): ExecutionResult =
    condition.isSatisfied(context)
        .flatMapBoolean(
            ifTrue = {
                resultWith {
                    val (target) = target.compute(context)
                    val (compareWith) = compareWith.compute(context)
                    val result = comparator.compare(target, compareWith)
                    if (result)
                        Success.asNull
                    else
                        this@execute.errorCode.asSuccess()
                }
            },
            ifFalse = { Success.asNull }
        )
