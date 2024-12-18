package io.github.ustudiocompany.uframework.rulesengine.core.rule

public data class Predicate(
    public val target: Value,
    public val comparator: Comparator,
    public val compareWith: Value
)
