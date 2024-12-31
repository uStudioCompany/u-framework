package io.github.ustudiocompany.uframework.rulesengine.core.rule.operator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal data object EqualOperator : AbstractOperator() {

    override fun DataElement.Null.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> true
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> false
    }

    override fun DataElement.Bool.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> this == value
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> false
    }

    override fun DataElement.Text.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> this == value
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> false
    }

    override fun DataElement.Decimal.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> this == value
        is DataElement.Struct -> false
        is DataElement.Array -> false
    }

    override fun DataElement.Struct.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> compareStructs(this, value)
        is DataElement.Array -> false
    }

    override fun DataElement.Array.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> compareArrays(this, value)
    }

    private fun compareStructs(target: DataElement.Struct, compareWith: DataElement.Struct): Boolean {
        if (target.size != compareWith.size) return false
        target.forEach { (key, targetValue) ->
            val compareWithValue = compareWith[key]
            if (compareWithValue == null || !apply(targetValue, compareWithValue)) return false
        }
        return true
    }

    private fun compareArrays(target: DataElement.Array, value: DataElement.Array): Boolean {
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
