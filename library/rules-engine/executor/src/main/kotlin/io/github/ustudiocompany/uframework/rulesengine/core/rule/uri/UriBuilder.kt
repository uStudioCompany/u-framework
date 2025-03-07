package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.flatMap
import io.github.ustudiocompany.uframework.rulesengine.executor.error.UriBuilderError
import java.net.URI

public fun UriTemplate.build(args: Map<String, String>): ResultK<URI, UriBuilderError> =
    replacePlaceholders(args)
        .flatMap {
            try {
                URI(it).asSuccess()
            } catch (expected: Exception) {
                UriBuilderError.InvalidUriTemplate(it, expected).asFailure()
            }
        }

private fun UriTemplate.replacePlaceholders(
    params: Map<String, String>
): ResultK<String, UriBuilderError.ParamMissing> = try {
    PLACEHOLDER_REG.replace(this) {
        val name = it.groupValues[1]
        params[name] ?: throw ArgMissingException(name)
    }.asSuccess()
} catch (expected: ArgMissingException) {
    UriBuilderError.ParamMissing(expected.name).asFailure()
}

private class ArgMissingException(val name: String) : Exception()

private val PLACEHOLDER_REG = """\{(\w+)\}""".toRegex()
