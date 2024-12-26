package io.github.ustudiocompany.uframework.rulesengine.core.rule.step

@JvmInline
public value class Steps(private val get: List<Step>) : List<Step> by get
