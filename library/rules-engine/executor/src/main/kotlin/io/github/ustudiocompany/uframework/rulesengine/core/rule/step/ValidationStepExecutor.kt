package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.flatMapBoolean
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.operation.calculate
import io.github.ustudiocompany.uframework.rulesengine.core.rule.condition.isSatisfied
import io.github.ustudiocompany.uframework.rulesengine.executor.ExecutionResult

internal fun ValidationStep.execute(context: Context): ExecutionResult {
    val operation = this@execute
    return condition.isSatisfied(context)
        .flatMapBoolean(
            ifTrue = {
                operation.calculate(context)
                    .flatMapBoolean(
                        ifTrue = { Success.asNull },
                        ifFalse = { errorCode.asSuccess() }
                    )
            },
            ifFalse = { Success.asNull }
        )
}
