package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.json.element.JsonElement

public class EnvVars private constructor(
    private val variables: MutableMap<EnvVarName, JsonElement>
) : Iterable<Pair<EnvVarName, JsonElement>> {

    public fun getOrNull(envVarName: EnvVarName): JsonElement? = variables[envVarName]

    public operator fun contains(envVarName: EnvVarName): Boolean = envVarName in variables

    override fun iterator(): Iterator<Pair<EnvVarName, JsonElement>> = EnvVarsIterator(variables)

    public companion object {
        public val EMPTY: EnvVars = EnvVars(variables = mutableMapOf())

        public operator fun invoke(vararg envVars: Pair<EnvVarName, JsonElement>): EnvVars =
            if (envVars.isNotEmpty())
                EnvVars(variables = envVars.toMap(mutableMapOf()))
            else
                EMPTY

        public operator fun invoke(envVars: Map<EnvVarName, JsonElement> = emptyMap()): EnvVars =
            if (envVars.isNotEmpty())
                EnvVars(variables = envVars.toMutableMap())
            else
                EMPTY
    }

    public class Builder {
        private val envVars: MutableMap<EnvVarName, JsonElement> = mutableMapOf()

        public operator fun set(envVarName: EnvVarName, value: JsonElement): Builder {
            envVars[envVarName] = value
            return this
        }

        public fun build(): EnvVars = if (envVars.isNotEmpty()) EnvVars(envVars) else EMPTY
    }

    private class EnvVarsIterator(
        data: Map<EnvVarName, JsonElement>
    ) : AbstractIterator<Pair<EnvVarName, JsonElement>>() {
        val original = data.iterator()
        override fun computeNext() {
            if (original.hasNext())
                setNext(original.next().toPair())
            else
                done()
        }
    }
}
