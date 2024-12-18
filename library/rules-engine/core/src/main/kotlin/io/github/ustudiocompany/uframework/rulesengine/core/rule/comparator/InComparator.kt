package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

internal val COMPARATOR_IN = ComparatorFun { target, compareWith ->
    when {
        target is DataElement.Text && compareWith is DataElement.Array -> target in compareWith
        target is DataElement.Array && compareWith is DataElement.Array -> target in compareWith
        else -> false
    }
}
