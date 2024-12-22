package io.github.ustudiocompany.uframework.rulesengine.core.rule

@JvmInline
public value class Rules(private val get: List<Rule>) : List<Rule> by get
