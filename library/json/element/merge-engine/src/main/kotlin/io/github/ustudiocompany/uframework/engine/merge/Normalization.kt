package io.github.ustudiocompany.uframework.engine.merge

import io.github.ustudiocompany.uframework.json.element.JsonElement

internal fun JsonElement.normalize(): JsonElement? {
    return when (this) {
        is JsonElement.Null -> null
        is JsonElement.Bool -> this
        is JsonElement.Text -> this
        is JsonElement.Decimal -> this
        is JsonElement.Array -> normalize()
        is JsonElement.Struct -> normalize()
    }
}

internal fun JsonElement.Array.normalize(): JsonElement? {
    val builder = JsonElement.Array.Builder()
    forEach { item ->
        val normalizedItem = item.normalize()
        if (normalizedItem != null) builder.add(normalizedItem)
    }
    return if (builder.hasItems) builder.build() else null
}

internal fun JsonElement.Struct.normalize(): JsonElement? {
    val builder = JsonElement.Struct.Builder()
    forEach { (key, value) ->
        val normalizedValue = value.normalize()
        if (normalizedValue != null) builder[key] = normalizedValue
    }
    return if (builder.hasProperties) builder.build() else null
}
