package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value

public interface Operation<T> {
    public val target: Value
    public val operator: Operator<T>
    public val value: Value
}
