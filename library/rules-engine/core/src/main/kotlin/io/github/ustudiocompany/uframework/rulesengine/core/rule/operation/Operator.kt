package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public fun interface Operator {
    public fun compute(target: DataElement?, value: DataElement?): Boolean
}
