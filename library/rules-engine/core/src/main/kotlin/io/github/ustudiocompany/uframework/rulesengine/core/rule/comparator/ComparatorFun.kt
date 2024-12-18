package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public fun interface ComparatorFun {
    public fun compare(target: DataElement, compareWith: DataElement): Boolean
}
