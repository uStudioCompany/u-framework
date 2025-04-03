package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.core.rule.step.call.Args
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider
import io.github.ustudiocompany.uframework.rulesengine.executor.error.RuleEngineError

internal fun Args.build(context: Context): ResultK<List<DataProvider.Arg>, RuleEngineError> = result {
    val args = this@build
    mutableListOf<DataProvider.Arg>()
        .apply {
            args.forEach { arg ->
                val value = arg.value.compute(context).bind()
                val argValue = value.toJson()
                add(DataProvider.Arg(arg.name, argValue))
            }
        }
}
