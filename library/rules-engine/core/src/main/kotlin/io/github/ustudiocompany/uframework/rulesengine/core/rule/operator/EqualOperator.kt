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
        target.forEach { (key, value) ->
            val compareWithValue = compareWith[key]
            if (compareWithValue == null) return false
            if (!apply(value, compareWithValue)) return false
        }
        return true
    }

    private fun compareArrays(target: DataElement.Array, compareWith: DataElement.Array): Boolean {
        if (target.size != compareWith.size) return false
        val groupedTarget = target.groupBy(keySelector = { it }, valueTransform = { it })
        val groupedCompareWith = compareWith.groupBy(keySelector = { it }, valueTransform = { it })

        groupedTarget.forEach { (key, value) ->
            val compareWithValue = groupedCompareWith[key]
            if (compareWithValue == null || value.size != compareWithValue.size) return false
        }
        return true
    }
}
