package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.rulesengine.core.rule.computeOrNull
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult

internal fun ValidationStep.execute(context: Context): ExecutionResult =
    condition.isSatisfied(context)
        .flatMapBoolean(
            ifTrue = {
                resultWith {
                    val (target) = target.computeOrNull(context)
                    val (compareWith) = compareWith.computeOrNull(context)
                    val result = operator.apply(target = target, value = compareWith)
                    if (result)
                        Success.asNull
                    else
                        this@execute.errorCode.asSuccess()
                }
            },
            ifFalse = { Success.asNull }
        )
