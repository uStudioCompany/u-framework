package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal data object IsPresentComparator : ComparatorFun {

    override fun invoke(target: DataElement?, compareWith: DataElement?): Boolean = when (target) {
        null -> false
        is DataElement.Null -> true
        is DataElement.Bool -> true
        is DataElement.Text -> true
        is DataElement.Decimal -> true
        is DataElement.Struct -> true
        is DataElement.Array -> true
    }
}
