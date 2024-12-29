package io.github.ustudiocompany.uframework.rulesengine.core.rule.header

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value

@JvmInline
public value class Headers(private val get: List<Header>) : List<Header> by get {

    public constructor(vararg headers: Pair<String, Value>) :
        this(headers.map { (name, value) -> Header(name, value) })
}
