package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal data object InOperator : AbstractOperator() {

    override fun DataElement.Null.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> value.any { it is DataElement.Null }
    }

    override fun DataElement.Bool.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> value.any { it is DataElement.Bool && this == it }
    }

    override fun DataElement.Text.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> value.any { it is DataElement.Text && this == it }
    }

    override fun DataElement.Decimal.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> value.any { it is DataElement.Decimal && this == it }
    }

    override fun DataElement.Struct.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> value.any { it is DataElement.Struct && this == it }
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

    private fun compareArrays(target: DataElement.Array, value: DataElement.Array): Boolean {
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
