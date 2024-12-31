package io.github.ustudiocompany.uframework.rulesengine.core.operation

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.rulesengine.core.rule.computeOrNull
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.Operation
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun Operation.calculate(context: Context): ResultK<Boolean, RuleEngineError> = result {
    val (target) = target.computeOrNull(context)
    val (value) = value.computeOrNull(context)
    operator.compute(target = target, value = value)
}
