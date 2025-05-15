package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator

import io.github.ustudiocompany.uframework.json.element.JsonElement

internal data object EqualOperator : AbstractBooleanOperator() {

    override fun JsonElement.Null.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> true
        is JsonElement.Bool -> false
        is JsonElement.Text -> false
        is JsonElement.Decimal -> false
        is JsonElement.Struct -> false
        is JsonElement.Array -> false
    }

    override fun JsonElement.Bool.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> false
        is JsonElement.Bool -> this == value
        is JsonElement.Text -> false
        is JsonElement.Decimal -> false
        is JsonElement.Struct -> false
        is JsonElement.Array -> false
    }

    override fun JsonElement.Text.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> false
        is JsonElement.Bool -> false
        is JsonElement.Text -> this == value
        is JsonElement.Decimal -> false
        is JsonElement.Struct -> false
        is JsonElement.Array -> false
    }

    override fun JsonElement.Decimal.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> false
        is JsonElement.Bool -> false
        is JsonElement.Text -> false
        is JsonElement.Decimal -> this == value
        is JsonElement.Struct -> false
        is JsonElement.Array -> false
    }

    override fun JsonElement.Struct.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> false
        is JsonElement.Bool -> false
        is JsonElement.Text -> false
        is JsonElement.Decimal -> false
        is JsonElement.Struct -> compareStructs(this, value)
        is JsonElement.Array -> false
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

    private fun compareStructs(target: JsonElement.Struct, compareWith: JsonElement.Struct): Boolean {
        if (target.size != compareWith.size) return false
        target.forEach { (key, targetValue) ->
            val compareWithValue = compareWith[key]
            if (compareWithValue == null || !compute(targetValue, compareWithValue)) return false
        }
        return true
    }

    private fun compareArrays(target: JsonElement.Array, value: JsonElement.Array): Boolean {
        if (target.size != value.size) return false
        val groupedTarget = target.groupBy(keySelector = { it }, valueTransform = { it })
        val groupedValue = value.groupBy(keySelector = { it }, valueTransform = { it })

        groupedTarget.forEach { (key, targetValues) ->
            val values = groupedValue[key]
            if (values == null || targetValues.size != values.size) return false
        }
        return true
    }
}
