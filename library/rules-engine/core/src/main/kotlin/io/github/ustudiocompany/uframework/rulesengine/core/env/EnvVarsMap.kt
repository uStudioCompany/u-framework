package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.json.element.JsonElement

public fun envVarsOf(vararg envVars: Pair<EnvVarName, JsonElement>): EnvVars {
    if (envVars.isEmpty()) return EnvVarsMap.EMPTY
    val builder = EnvVarsMap.Builder()
    for ((name, value) in envVars)
        builder[name] = value
    return builder.build()
}

private class EnvVarsMap private constructor(
    private val variables: Map<EnvVarName, JsonElement>
) : EnvVars {

    override fun isEmpty(): Boolean = variables.isEmpty()

    override fun getOrNull(name: EnvVarName): JsonElement? = variables[name]

    override operator fun contains(name: EnvVarName): Boolean = name in variables

    override fun iterator(): Iterator<EnvVars.Variable> = EnvVarsIterator()

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

    private inner class EnvVarsIterator : AbstractIterator<EnvVars.Variable>() {
        val iter = variables.iterator()
        override fun computeNext() {
            if (iter.hasNext()) {
                val item = iter.next()
                setNext(EnvVars.Variable(name = item.key, value = item.value))
            } else
                done()
        }
    }
}
