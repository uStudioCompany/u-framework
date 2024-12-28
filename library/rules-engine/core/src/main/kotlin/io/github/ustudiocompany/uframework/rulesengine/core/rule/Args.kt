package io.github.ustudiocompany.uframework.rulesengine.core.rule

@JvmInline
public value class Args(private val get: List<Arg>) : List<Arg> by get
