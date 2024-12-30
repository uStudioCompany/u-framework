package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal data object InComparator : ComparatorFun {

    override fun invoke(target: DataElement?, compareWith: DataElement?): Boolean = when {
        target is DataElement.Text && compareWith is DataElement.Array -> target in compareWith
        target is DataElement.Array && compareWith is DataElement.Array -> target in compareWith
        else -> false
    }
}
