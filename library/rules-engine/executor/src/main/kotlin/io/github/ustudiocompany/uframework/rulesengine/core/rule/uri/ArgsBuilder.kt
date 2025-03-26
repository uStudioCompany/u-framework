package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.toPlainString
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.executor.CallProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun Args.build(context: Context): ResultK<List<CallProvider.Request.Arg>, RuleEngineError> {
    val values = mutableListOf<CallProvider.Request.Arg>()
    forEach { arg ->
        val value = arg.value.compute(context).getOrForward { return it }
        values.add(CallProvider.Request.Arg(arg.name, value.toPlainString()))
    }
    return values.asSuccess()
}
