package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputeErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute

internal fun <T> Args.build(
    envVars: EnvVars,
    context: Context,
    builder: (name: String, value: String) -> T
): ResultK<List<T>, ArgsBuilderErrors> =
    result {
        val args = this@build
        mutableListOf<T>()
            .apply {
                args.get.forEach { arg ->
                    val (value) = arg.value.compute(envVars, context)
                        .mapFailure { failure ->
                            ArgsBuilderErrors.ArgValueBuilding(arg = arg, cause = failure)
                        }
                    val argValue = if (value is JsonElement.Text)
                        value.toString()
                    else
                        value.toJson()
                    add(builder(arg.name, argValue))
                }
            }
    }

internal sealed interface ArgsBuilderErrors : BasicRulesEngineError {

    class ArgValueBuilding(arg: Arg, cause: ValueComputeErrors) : ArgsBuilderErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error building arg '${arg.name}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "ARGS-BUILDER-"
    }
}
