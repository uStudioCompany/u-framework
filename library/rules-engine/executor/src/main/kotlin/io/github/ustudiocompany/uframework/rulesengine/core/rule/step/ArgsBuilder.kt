package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.isFailure
import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Arg
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Args
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun Args.build(
    context: Context,
    argType: Arg.Type
): ResultK<Map<String, DataElement>, RuleEngineError> {
    val argValues = mutableMapOf<String, DataElement>()
    forEach { arg ->
        if (arg.type == argType) {
            val result = arg.value.compute(context)
            if (result.isFailure()) return result
            argValues[arg.name] = result.value
        }
    }
    return argValues.asSuccess()
}
