package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.airflux.commons.types.resultk.result
import io.github.ustudiocompany.uframework.failure.Failure
import io.github.ustudiocompany.uframework.rulesengine.core.BasicRulesEngineError
import io.github.ustudiocompany.uframework.rulesengine.core.context.Context
import io.github.ustudiocompany.uframework.rulesengine.core.data.toStringValue
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVars
import io.github.ustudiocompany.uframework.rulesengine.core.rule.ValueComputeErrors
import io.github.ustudiocompany.uframework.rulesengine.core.rule.compute

internal fun <T> MessageHeaders.build(
    envVars: EnvVars,
    context: Context,
    builder: (name: String, value: String) -> T
): ResultK<List<T>, ArgsBuilderErrors> =
    result {
        val headers = this@build
        mutableListOf<T>()
            .apply {
                headers.get.forEach { header ->
                    val (value) = header.value.compute(envVars, context)
                        .mapFailure { failure ->
                            HeadersBuilderErrors.MessageHeaderValueBuilding(header = header, cause = failure)
                        }
                    val headerValue = value.toStringValue()
                    add(builder(header.name, headerValue))
                }
            }
    }

internal sealed interface HeadersBuilderErrors : BasicRulesEngineError {

    class MessageHeaderValueBuilding(header: MessageHeader, cause: ValueComputeErrors) : ArgsBuilderErrors {
        override val code: String = PREFIX + "1"
        override val description: String = "Error building the header '${header.name}'."
        override val cause: Failure.Cause = Failure.Cause.Failure(cause)
    }

    private companion object {
        private const val PREFIX = "MESSAGE_HEADERS-BUILDER-"
    }
}
