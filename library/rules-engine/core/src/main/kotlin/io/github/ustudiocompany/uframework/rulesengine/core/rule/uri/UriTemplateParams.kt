package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value

@JvmInline
public value class UriTemplateParams(private val get: List<UriTemplateParam>) : List<UriTemplateParam> by get {

    public constructor(vararg params: Pair<String, Value>) :
        this(params.map { (name, value) -> UriTemplateParam(name, value) })
}
