package io.github.ustudiocompany.uframework.rulesengine.core.rule.condition

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value
import io.github.ustudiocompany.uframework.rulesengine.core.rule.comparator.Comparator

public data class Predicate(
    public val target: Value,
    public val comparator: Comparator,
    public val compareWith: Value
)
