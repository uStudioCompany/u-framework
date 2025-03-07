package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal data object ContainsOperator : AbstractBooleanOperator() {
    override fun DataElement.Null.compareWith(value: DataElement?): Boolean = false
    override fun DataElement.Bool.compareWith(value: DataElement?): Boolean = false
    override fun DataElement.Text.compareWith(value: DataElement?): Boolean = false
    override fun DataElement.Decimal.compareWith(value: DataElement?): Boolean = false
    override fun DataElement.Struct.compareWith(value: DataElement?): Boolean = false
    override fun DataElement.Array.compareWith(value: DataElement?): Boolean = when (value) {
        null -> false
        is DataElement.Null -> any { it is DataElement.Null }
        is DataElement.Bool -> any { it is DataElement.Null && it == value }
        is DataElement.Text -> any { it is DataElement.Null && it == value }
        is DataElement.Decimal -> any { it is DataElement.Null && it == value }
        is DataElement.Struct -> false
        is DataElement.Array -> compareArrays(value, this)
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
