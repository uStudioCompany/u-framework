package io.github.ustudiocompany.uframework.rulesengine.core.rule.header

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.toPlainString
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.executor.CallProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun Headers.build(context: Context): ResultK<List<CallProvider.Request.Header>, RuleEngineError> {
    val values = mutableListOf<CallProvider.Request.Header>()
    forEach { header ->
        val value = header.value.compute(context).getOrForward { return it }
        values.add(CallProvider.Request.Header(header.name, value.toPlainString()))
    }
    return values.asSuccess()
}
