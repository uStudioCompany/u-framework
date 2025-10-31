package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.json.element.JsonElement

public fun envVarsMapOf(vararg envVars: Pair<EnvVarName, JsonElement>): EnvVars = envVars.asEnvVarsMap()

public fun Array<out Pair<EnvVarName, JsonElement>>.asEnvVarsMap(): EnvVars {
    if (isEmpty()) return EnvVarsMap.EMPTY
    val builder = EnvVarsMap.Builder()
    for ((name, value) in this)
        builder[name] = value
    return builder.build()
}

private class EnvVarsMap private constructor(
    private val variables: Map<EnvVarName, JsonElement>
) : EnvVars {

    override fun isEmpty(): Boolean = variables.isEmpty()

    override fun getOrNull(name: EnvVarName): JsonElement? = variables[name]

    override operator fun contains(name: EnvVarName): Boolean = name in variables

    override fun iterator(): Iterator<Pair<EnvVarName, JsonElement>> = EnvVarsIterator()

    companion object {
        val EMPTY: EnvVars = EnvVarsMap(variables = mapOf())
    }

    class Builder {
        private val envVars: MutableMap<EnvVarName, JsonElement> = mutableMapOf()

        operator fun set(envVarName: EnvVarName, value: JsonElement): Builder {
            envVars[envVarName] = value
            return this
        }

        fun build(): EnvVars = if (envVars.isNotEmpty()) EnvVarsMap(envVars) else EMPTY
    }

    private inner class EnvVarsIterator : AbstractIterator<Pair<EnvVarName, JsonElement>>() {
        val iter = variables.iterator()
        override fun computeNext() {
            if (iter.hasNext())
                setNext(iter.next().toPair())
            else
                done()
        }
    }
}
