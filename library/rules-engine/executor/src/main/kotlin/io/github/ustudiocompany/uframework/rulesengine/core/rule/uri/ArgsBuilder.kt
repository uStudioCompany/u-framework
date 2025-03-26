package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.toPlainString
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.executor.CallProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun Args.build(context: Context): ResultK<List<CallProvider.Request.Arg>, RuleEngineError> = result {
    val args = this@build
    mutableListOf<CallProvider.Request.Arg>()
        .apply {
            args.forEach { arg ->
                val value = arg.value.compute(context).bind()
                val argValue = value.toPlainString()
                if (argValue != null)
                    add(CallProvider.Request.Arg(arg.name, argValue))
            }
        }
}
