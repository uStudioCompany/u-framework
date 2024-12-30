package io.github.ustudiocompany.uframework.rulesengine.core.rule.operator

import io.github.ustudiocompany.uframework.rulesengine.core.data.DataElement

public fun interface Operator {
    public fun apply(target: DataElement?, value: DataElement?): Boolean
}
