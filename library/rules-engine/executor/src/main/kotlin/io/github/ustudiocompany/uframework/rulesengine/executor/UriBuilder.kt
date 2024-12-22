package io.github.ustudiocompany.uframework.rulesengine.executor

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement
import java.net.URI

public class UriBuilder(private var template: String) {
    private var pathParams: Map<String, DataElement> = emptyMap()
    private var queryParams: Map<String, DataElement> = emptyMap()

    public fun pathParams(params: Map<String, DataElement>): UriBuilder {
        pathParams = params
        return this
    }

    public fun queryParams(params: Map<String, DataElement>): UriBuilder {
        queryParams = params
        return this
    }

    public fun build(): URI =
        template.replacePlaceholders(pathParams)
            .addQueryParams(queryParams)
            .let { URI(it) }

    private fun String.replacePlaceholders(params: Map<String, DataElement>) = PLACEHOLDER_REG.replace(this) {
        val name = it.groupValues[1]
        params[name]?.toPlainString()
            ?: error("Argument not found: $name")
    }

    private fun String.addQueryParams(params: Map<String, DataElement>) =
        if (params.isNotEmpty())
            this + params.asIterable()
                .joinToString(prefix = "?", separator = "&") { (name, value) ->
                    "$name=${value.toPlainString()}"
                }
        else
            this

    private fun DataElement.toPlainString() = when (this) {
        is DataElement.Text -> this.get
        is DataElement.Decimal -> this.get.toPlainString()
        else -> error("Invalid argument type: $this")
    }

    private companion object {
        private val PLACEHOLDER_REG = """\{(\w+)\}""".toRegex()
    }
}
