package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.toStringValue
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputationErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute

internal fun <T> Args.build(
    envVars: EnvVars,
    context: Context,
    builder: (name: String, value: String) -> T
): ResultK<List<T>, ArgBuildErrors> =
    result {
        val args = this@build
        mutableListOf<T>()
            .apply {
                args.get.forEach { arg ->
                    val (value) = arg.value.compute(envVars, context)
                        .mapFailure { failure ->
                            ArgBuildErrors.ValueComputation(arg = arg, cause = failure)
                        }
                    val argValue = value.toStringValue()
                    add(builder(arg.name, argValue))
                }
            }
    }

internal sealed interface ArgBuildErrors : BasicRulesEngineError {

    class ValueComputation(arg: Arg, cause: ValueComputationErrors) : ArgBuildErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error in computation the argument value '${arg.name}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "ARG-BUILD-"
    }
}
