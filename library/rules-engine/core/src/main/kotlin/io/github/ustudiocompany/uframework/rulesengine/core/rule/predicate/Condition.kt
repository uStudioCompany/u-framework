package io.github.ustudiocompany.uframework.rulesengine.core.rule.predicate

@JvmInline
public value class Condition(private val get: List<Predicate>) : List<Predicate> by get
