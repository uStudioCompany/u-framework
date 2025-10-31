package io.github.ustudiocompany.uframework.rulesengine.core.env

import io.github.ustudiocompany.uframework.json.element.JsonElement

internal fun EnvVars.append(vararg envVars: Pair<EnvVarName, JsonElement>): EnvVars =
    if (envVars.isNotEmpty())
        ExtendedEnvVars(appended = envVars.asEnvVarsList(), origin = this)
    else
        this

private class ExtendedEnvVars(private val appended: EnvVars, private val origin: EnvVars) : EnvVars {
    override fun isEmpty(): Boolean = appended.isEmpty() && origin.isEmpty()

    override fun getOrNull(name: EnvVarName): JsonElement? =
        appended.getOrNull(name) ?: origin.getOrNull(name)

    override fun contains(name: EnvVarName): Boolean =
        name in appended || name in origin

    override fun iterator(): Iterator<Pair<EnvVarName, JsonElement>> = AppendedEnvVarsIterator()

    private inner class AppendedEnvVarsIterator : AbstractIterator<Pair<EnvVarName, JsonElement>>() {
        var appendedIter = appended.iterator()
        var originIter = origin.iterator()

        override fun computeNext() {
            if (appendedIter.hasNext())
                setNext(appendedIter.next())
            else if (originIter.hasNext())
                setNext(originIter.next())
            else
                done()
        }
    }
}
