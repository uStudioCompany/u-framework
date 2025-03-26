package io.github.ustudiocompany.uframework.rulesengine.core.rule.uri

import io.github.ustudiocompany.uframework.rulesengine.core.rule.Value

@JvmInline
public value class Args(private val get: List<Arg>) : List<Arg> by get {

    public constructor(vararg params: Pair<String, Value>) :
        this(params.map { (name, value) -> Arg(name, value) })
}
