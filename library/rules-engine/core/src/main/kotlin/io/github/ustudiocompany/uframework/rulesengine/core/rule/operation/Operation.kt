package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value

public interface Operation {
    public val target: Value
    public val operator: Operator
    public val value: Value
}
