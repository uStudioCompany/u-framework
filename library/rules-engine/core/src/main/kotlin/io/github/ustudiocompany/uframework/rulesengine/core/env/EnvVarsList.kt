package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarsList.Companion.EMPTY

public fun envVarsListOf(vararg envVars: Pair<EnvVarName, JsonElement>): EnvVars = envVars.asEnvVarsList()

public fun Array<out Pair<EnvVarName, JsonElement>>.asEnvVarsList(): EnvVars {
    if (isEmpty()) return EMPTY
    val builder = EnvVarsList.Builder()
    for ((name, value) in this)
        builder[name] = value
    return builder.build()
}

private class EnvVarsList private constructor(
    private val variables: List<Pair<EnvVarName, JsonElement>>
) : EnvVars {

    override fun isEmpty(): Boolean = variables.isEmpty()

    override fun getOrNull(name: EnvVarName): JsonElement? = variables.find { it.first == name }?.second

    override operator fun contains(name: EnvVarName): Boolean = variables.any { it.first == name }

    override fun iterator(): Iterator<Pair<EnvVarName, JsonElement>> = EnvVarsIterator()

    companion object {
        val EMPTY: EnvVars = EnvVarsList(variables = emptyList())
    }

    class Builder {
        private val envVars: MutableList<Pair<EnvVarName, JsonElement>> = mutableListOf()

        operator fun set(envVarName: EnvVarName, value: JsonElement): Builder {
            val old = envVars.indexOfFirst { it.first == envVarName }
            if (old >= 0)
                envVars[old] = Pair(envVarName, value)
            else
                envVars.add(Pair(envVarName, value))
            return this
        }

        fun build(): EnvVars = if (envVars.isNotEmpty()) EnvVarsList(envVars) else EMPTY
    }

    private inner class EnvVarsIterator : AbstractIterator<Pair<EnvVarName, JsonElement>>() {
        val iter = variables.iterator()
        override fun computeNext() {
            if (iter.hasNext())
                setNext(iter.next())
            else
                done()
        }
    }
}
