package io.github.ustudiocompany.uframework.rulesengine.core.rule.operation

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operation.operator.Operator

public interface Operation<T> {
    public val target: Value
    public val operator: Operator<T>
    public val value: Value?
}
