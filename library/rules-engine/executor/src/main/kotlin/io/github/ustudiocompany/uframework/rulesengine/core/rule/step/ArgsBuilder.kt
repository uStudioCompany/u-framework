package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputeErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute
import io.github.ustudiocompany.uframework.rulesengine.executor.DataProvider

internal fun Args.build(context: Context): ResultK<List<DataProvider.Arg>, DataProviderArgsErrors> = result {
    val args = this@build
    mutableListOf<DataProvider.Arg>()
        .apply {
            args.get.forEach { arg ->
                val (value) = arg.value.compute(context)
                    .mapFailure { failure ->
                        DataProviderArgsErrors.ArgValueBuild(arg = arg, cause = failure)
                    }
                val argValue = value.toJson()
                add(DataProvider.Arg(arg.name, argValue))
            }
        }
}

internal sealed interface DataProviderArgsErrors : BasicRulesEngineError {

    class ArgValueBuild(arg: Arg, cause: ValueComputeErrors) : DataProviderArgsErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error building arg '${arg.name}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "DATA-PROVIDER-ARGS-"
    }
}
