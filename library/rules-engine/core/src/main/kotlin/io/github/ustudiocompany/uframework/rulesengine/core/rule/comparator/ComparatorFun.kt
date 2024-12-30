package io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public fun interface ComparatorFun {
    public operator fun invoke(target: DataElement?, compareWith: DataElement?): Boolean
}
