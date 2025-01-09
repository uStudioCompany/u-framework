package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.rulesengine.core.data.toPlainString
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.executor.context.Context
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun UriTemplateParams.build(context: Context): ResultK<Map<String, String>, RuleEngineError> {
    val values = mutableMapOf<String, String>()
    forEach { param ->
        val value = param.value.compute(context).getOrForward { return it }
        values[param.name] = value.toPlainString()
    }
    return values.asSuccess()
}
