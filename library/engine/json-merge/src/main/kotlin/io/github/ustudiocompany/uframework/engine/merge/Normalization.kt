package io.github.ustudiocompany.uframework.engine.merge

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal fun DataElement.normalize(): DataElement? {
    return when (this) {
        is DataElement.Null -> null
        is DataElement.Bool -> this
        is DataElement.Text -> this
        is DataElement.Decimal -> this
        is DataElement.Array -> normalize()
        is DataElement.Struct -> normalize()
    }
}

internal fun DataElement.Array.normalize(): DataElement? {
    val builder = DataElement.Array.Builder()
    forEach { item ->
        val normalizedItem = item.normalize()
        if (normalizedItem != null) builder.add(normalizedItem)
    }
    return if (builder.hasItems) builder.build() else null
}

internal fun DataElement.Struct.normalize(): DataElement? {
    val builder = DataElement.Struct.Builder()
    forEach { (key, value) ->
        val normalizedValue = value.normalize()
        if (normalizedValue != null) builder[key] = normalizedValue
    }
    return if (builder.hasProperties) builder.build() else null
}
