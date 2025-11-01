package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.json.element.JsonElement

public interface EnvVars : Iterable<EnvVars.Variable> {
    public fun isEmpty(): Boolean
    public fun isNotEmpty(): Boolean = !isEmpty()
    public fun getOrNull(name: EnvVarName): JsonElement?
    public operator fun contains(name: EnvVarName): Boolean

    public data class Variable(val name: EnvVarName, val value: JsonElement)
}
