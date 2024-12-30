package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal data object InComparator : AbstractComparator() {

    override fun invoke(target: DataElement?, value: DataElement?): Boolean = when (target) {
        null -> false
        is DataElement.Null -> target.compareWith(value)
        is DataElement.Bool -> target.compareWith(value)
        is DataElement.Text -> target.compareWith(value)
        is DataElement.Decimal -> target.compareWith(value)
        is DataElement.Struct -> target.compareWith(value)
        is DataElement.Array -> target.compareWith(value)
    }

    private fun DataElement.Null.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> value.any { it is DataElement.Null }
    }

    private fun DataElement.Bool.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> value.any { it is DataElement.Bool && this == it }
    }

    private fun DataElement.Text.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> value.any { it is DataElement.Text && this == it }
    }

    private fun DataElement.Decimal.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> value.any { it is DataElement.Decimal && this == it }
    }

    private fun DataElement.Struct.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> false
        is DataElement.Bool -> false
        is DataElement.Text -> false
        is DataElement.Decimal -> false
        is DataElement.Struct -> false
        is DataElement.Array -> value.any { it is DataElement.Struct && this == it }
    }

    private fun DataElement.Array.compareWith(value: DataElement?): Boolean = when (value) {
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
