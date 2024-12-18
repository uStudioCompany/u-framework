package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal val COMPARATOR_EQ = ComparatorFun { target, compareWith ->
    when {
        target is DataElement.Text && compareWith is DataElement.Text -> target == compareWith
        target is DataElement.Decimal && compareWith is DataElement.Decimal -> target == compareWith
        else -> false
    }
}
