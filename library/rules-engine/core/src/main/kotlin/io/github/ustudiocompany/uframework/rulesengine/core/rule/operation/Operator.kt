package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public fun interface Operator<T> {
    public fun compute(target: DataElement?, value: DataElement?): T
}
