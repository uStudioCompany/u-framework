package io.github.ustudiocompany.uframework.rulesengine.core.rule

@JvmInline
public value class Predicates(private val get: List<Predicate>) : List<Predicate> by get
