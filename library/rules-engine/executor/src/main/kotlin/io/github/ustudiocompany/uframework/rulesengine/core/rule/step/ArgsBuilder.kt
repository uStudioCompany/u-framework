package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.call.Args
import io.github.ustudiocompany.uframework.rulesengine.executor.CallProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun Args.build(context: Context): ResultK<List<CallProvider.Arg>, RuleEngineError> = result {
    val args = this@build
    mutableListOf<CallProvider.Arg>()
        .apply {
            args.forEach { arg ->
                val value = arg.value.compute(context).bind()
                val argValue = value.toJson()
                add(CallProvider.Arg(arg.name, argValue))
            }
        }
}
