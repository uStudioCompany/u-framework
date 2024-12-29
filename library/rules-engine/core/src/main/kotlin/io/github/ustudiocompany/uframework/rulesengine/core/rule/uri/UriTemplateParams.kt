package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

@JvmInline
public value class UriTemplateParams(private val get: List<UriTemplateParam>) : List<UriTemplateParam> by get
