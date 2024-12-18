package io.github.ustudiocompany.uframework.rulesengine.core.rule

public data class Rule(
    public val predicate: Predicates?,
    public val steps: List<Step>
)
