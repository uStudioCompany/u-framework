package io.github.ustudiocompany.uframework.jdbc.sql

public class ParametrizedSql private constructor(
    public val value: String,
    public val parameters: Map<String, Int>
) {

    public companion object {

        public fun of(sql: String): ParametrizedSql = parse(sql)

        private fun parse(value: String): ParametrizedSql {
            val stream = Stream(value)

            val parameters = mutableMapOf<String, Int>()
            val canonicalSqlBuilder = StringBuilder(value.length)
            while (stream.hasNext()) {
                val currentChar = stream.currentChar
                if (currentChar == ':') {
                    stream.next()
                    val name = stream.getParameterName()
                    parameters.append(name)
                    canonicalSqlBuilder.append(PARAMETER_NAME_PLACEHOLDER)
                } else {
                    canonicalSqlBuilder.append(currentChar)
                    stream.next()
                }
            }

            return ParametrizedSql(canonicalSqlBuilder.toString(), parameters)
        }

        private fun MutableMap<String, Int>.append(name: String) {
            val previous = this.put(name, size + 1)
            if (previous != null) error("The parameter `$name` is duplicated.")
        }

        private fun Stream.getParameterName(): String {
            val nameBuilder = StringBuilder()
            while (hasNext()) {
                val currentChar = this.currentChar
                if (currentChar.isAllowedParameterNameCharacter()) {
                    nameBuilder.append(currentChar)
                    next()
                } else
                    break
            }

            val name = nameBuilder.toString()
            if (name.isEmpty()) error("The parameter name is empty.")
            return name
        }

        private fun Char.isAllowedParameterNameCharacter(): Boolean =
            isLetterOrDigit() || this == '_' || this == '-'

        private const val PARAMETER_NAME_PLACEHOLDER = "?"
    }

    private class Stream(private val value: String) {
        val currentChar: Char
            get() = value[position]

        fun next() {
            position += 1
        }

        fun hasNext(): Boolean = position < value.length

        private var position = 0
    }
}
