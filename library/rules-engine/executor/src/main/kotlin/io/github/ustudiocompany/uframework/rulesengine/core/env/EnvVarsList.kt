package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.json.element.JsonElement
import io.github.ustudiocompany.uframework.rulesengine.core.env.EnvVarsList.Companion.EMPTY

internal fun envVarsListOf(vararg envVars: Pair<EnvVarName, JsonElement>): EnvVars = envVars.asEnvVarsList()

internal fun Array<out Pair<EnvVarName, JsonElement>>.asEnvVarsList(): EnvVars {
    if (isEmpty()) return EMPTY
    val builder = EnvVarsList.Builder()
    for ((name, value) in this)
        builder[name] = value
    return builder.build()
}

private class EnvVarsList private constructor(
    private val variables: List<EnvVars.Variable>
) : EnvVars {

    override fun isEmpty(): Boolean = variables.isEmpty()

    override fun getOrNull(name: EnvVarName): JsonElement? = variables.find { it.name == name }?.value

    override operator fun contains(name: EnvVarName): Boolean = variables.any { it.name == name }

    override fun iterator(): Iterator<EnvVars.Variable> = EnvVarsIterator()

    companion object {
        val EMPTY: EnvVars = EnvVarsList(variables = emptyList())
    }

    class Builder {
        private val envVars: MutableList<EnvVars.Variable> = mutableListOf()

        operator fun set(envVarName: EnvVarName, value: JsonElement): Builder {
            val old = envVars.indexOfFirst { it.name == envVarName }
            if (old >= 0)
                envVars[old] = EnvVars.Variable(envVarName, value)
            else
                envVars.add(EnvVars.Variable(envVarName, value))
            return this
        }

        fun build(): EnvVars = if (envVars.isNotEmpty()) EnvVarsList(envVars) else EMPTY
    }

    private inner class EnvVarsIterator : AbstractIterator<EnvVars.Variable>() {
        val iter = variables.iterator()
        override fun computeNext() {
            if (iter.hasNext())
                setNext(iter.next())
            else
                done()
        }
    }
}
