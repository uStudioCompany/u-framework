package io.github.ustudiocompany.uframework.rulesengine.core.rule.header

@JvmInline
public value class Headers(private val get: List<Header>) : List<Header> by get
