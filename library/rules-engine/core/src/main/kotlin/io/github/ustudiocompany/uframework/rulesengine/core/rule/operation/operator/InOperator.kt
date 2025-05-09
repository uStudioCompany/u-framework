package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator

import io.github.ustudiocompany.uframework.json.element.JsonElement

internal data object InOperator : AbstractBooleanOperator() {

    override fun JsonElement.Null.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> false
        is JsonElement.Bool -> false
        is JsonElement.Text -> false
        is JsonElement.Decimal -> false
        is JsonElement.Struct -> false
        is JsonElement.Array -> value.any { it is JsonElement.Null }
    }

    override fun JsonElement.Bool.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> false
        is JsonElement.Bool -> false
        is JsonElement.Text -> false
        is JsonElement.Decimal -> false
        is JsonElement.Struct -> false
        is JsonElement.Array -> value.any { it is JsonElement.Bool && this == it }
    }

    override fun JsonElement.Text.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> false
        is JsonElement.Bool -> false
        is JsonElement.Text -> false
        is JsonElement.Decimal -> false
        is JsonElement.Struct -> false
        is JsonElement.Array -> value.any { it is JsonElement.Text && this == it }
    }

    override fun JsonElement.Decimal.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> false
        is JsonElement.Bool -> false
        is JsonElement.Text -> false
        is JsonElement.Decimal -> false
        is JsonElement.Struct -> false
        is JsonElement.Array -> value.any { it is JsonElement.Decimal && this == it }
    }

    override fun JsonElement.Struct.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> false
        is JsonElement.Bool -> false
        is JsonElement.Text -> false
        is JsonElement.Decimal -> false
        is JsonElement.Struct -> false
        is JsonElement.Array -> value.any { it is JsonElement.Struct && this == it }
    }

    override fun JsonElement.Array.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> false
        is JsonElement.Bool -> false
        is JsonElement.Text -> false
        is JsonElement.Decimal -> false
        is JsonElement.Struct -> false
        is JsonElement.Array -> compareArrays(this, value)
    }

    private fun compareArrays(target: JsonElement.Array, value: JsonElement.Array): Boolean {
        if (target.size > value.size) return false
        val groupedTarget = target.groupBy(keySelector = { it }, valueTransform = { it })
        val groupedValue = value.groupBy(keySelector = { it }, valueTransform = { it })

        groupedTarget.forEach { (key, targetValues) ->
            val values = groupedValue[key]
            if (values == null || targetValues.size > values.size) return false
        }
        return true
    }
}
