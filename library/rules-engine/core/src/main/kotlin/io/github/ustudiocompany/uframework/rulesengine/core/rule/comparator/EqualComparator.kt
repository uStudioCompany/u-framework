package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal data object EqualComparator : ComparatorFun {

    @Suppress("CyclomaticComplexMethod")
    override fun invoke(target: DataElement?, compareWith: DataElement?): Boolean = when (target) {
        null -> false

        is DataElement.Null -> when (compareWith) {
            null -> false
            is DataElement.Null -> true
            is DataElement.Bool -> false
            is DataElement.Text -> false
            is DataElement.Decimal -> false
            is DataElement.Struct -> false
            is DataElement.Array -> false
        }

        is DataElement.Bool -> when (compareWith) {
            null -> false
            is DataElement.Null -> false
            is DataElement.Bool -> target.get == compareWith.get
            is DataElement.Text -> false
            is DataElement.Decimal -> false
            is DataElement.Struct -> false
            is DataElement.Array -> false
        }

        is DataElement.Text -> when (compareWith) {
            null -> false
            is DataElement.Null -> false
            is DataElement.Bool -> false
            is DataElement.Text -> target.get == compareWith.get
            is DataElement.Decimal -> false
            is DataElement.Struct -> false
            is DataElement.Array -> false
        }

        is DataElement.Decimal -> when (compareWith) {
            null -> false
            is DataElement.Null -> false
            is DataElement.Bool -> false
            is DataElement.Text -> false
            is DataElement.Decimal -> target.get.compareTo(compareWith.get) == 0
            is DataElement.Struct -> false
            is DataElement.Array -> false
        }

        is DataElement.Struct -> when (compareWith) {
            null -> false
            is DataElement.Null -> false
            is DataElement.Bool -> false
            is DataElement.Text -> false
            is DataElement.Decimal -> false
            is DataElement.Struct -> compareStructs(target, compareWith)
            is DataElement.Array -> false
        }

        is DataElement.Array -> when (compareWith) {
            null -> false
            is DataElement.Null -> false
            is DataElement.Bool -> false
            is DataElement.Text -> false
            is DataElement.Decimal -> false
            is DataElement.Struct -> false
            is DataElement.Array -> compareArrays(target, compareWith)
        }
    }

    private fun compareStructs(target: DataElement.Struct, compareWith: DataElement.Struct): Boolean {
        if (target.size != compareWith.size) return false
        target.forEach { (key, value) ->
            val compareWithValue = compareWith[key]
            if (compareWithValue == null) return false
            if (!invoke(value, compareWithValue)) return false
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
