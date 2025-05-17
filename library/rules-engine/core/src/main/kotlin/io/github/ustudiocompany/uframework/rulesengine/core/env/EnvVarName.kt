package io.github.ustudiocompany.uframework.rulesengine.core.env

public class EnvVarName(public val get: String) {
    private val hashCode = get.lowercase().hashCode()

    override fun equals(other: Any?): Boolean =
        if (this === other)
            EQUAL
        else if (other is EnvVarName)
            this.get.equals(other.get, ignoreCase = true)
        else
            NOT_EQUAL

    override fun hashCode(): Int = hashCode

    override fun toString(): String = get

    private companion object {
        private const val EQUAL = true
        private const val NOT_EQUAL = false
    }
}
