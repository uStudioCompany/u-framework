package io.github.ustudiocompany.uframework.rulesengine.core.rule.condition

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.operator.Operator

public data class Predicate(
    public val target: Value,
    public val operator: Operator,
    public val value: Value
)
