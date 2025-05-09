package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator

import io.github.ustudiocompany.uframework.json.element.JsonElement

internal data object ContainsOperator : AbstractBooleanOperator() {
    override fun JsonElement.Null.compareWith(value: JsonElement?): Boolean = false
    override fun JsonElement.Bool.compareWith(value: JsonElement?): Boolean = false
    override fun JsonElement.Text.compareWith(value: JsonElement?): Boolean = false
    override fun JsonElement.Decimal.compareWith(value: JsonElement?): Boolean = false
    override fun JsonElement.Struct.compareWith(value: JsonElement?): Boolean = false
    override fun JsonElement.Array.compareWith(value: JsonElement?): Boolean = when (value) {
        null -> false
        is JsonElement.Null -> any { it is JsonElement.Null }
        is JsonElement.Bool -> any { it is JsonElement.Null && it == value }
        is JsonElement.Text -> any { it is JsonElement.Null && it == value }
        is JsonElement.Decimal -> any { it is JsonElement.Null && it == value }
        is JsonElement.Struct -> false
        is JsonElement.Array -> compareArrays(value, this)
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
