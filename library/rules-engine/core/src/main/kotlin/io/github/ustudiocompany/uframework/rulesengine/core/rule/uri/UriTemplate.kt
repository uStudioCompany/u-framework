package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

@JvmInline
public value class UriTemplate(private val get: String) : CharSequence by get
