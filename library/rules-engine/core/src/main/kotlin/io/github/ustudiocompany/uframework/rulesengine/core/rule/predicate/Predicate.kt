package io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Comparator
import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value

public data class Predicate(
    public val target: Value,
    public val comparator: Comparator,
    public val compareWith: Value
)
