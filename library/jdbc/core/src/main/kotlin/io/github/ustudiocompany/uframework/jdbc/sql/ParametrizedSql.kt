package io.github.ustudiocompany.uframework.jdbc.sql

public class ParametrizedSql private constructor(
    public val value: String,
    public val parameters: Map<String, Int>
) {

    public companion object {
        private const val PARAMETER_NAME_PLACEHOLDER = "?"
        private const val PREFIX_PARAMETER_NAME = ':'

        public fun of(sql: String): ParametrizedSql = parse(sql)

        private fun parse(value: String): ParametrizedSql {
            val stream = Stream(value)

            val parameters = mutableMapOf<String, Int>()
            val canonicalSqlBuilder = StringBuilder(value.length)
            val parameterNameBuilder = StringBuilder()

            while (stream.hasNextChar()) {
                val currentChar = stream.currentChar
                if (currentChar == PREFIX_PARAMETER_NAME) {
                    stream.moveToNextChar()
                    val name = stream.getParameterName(parameterNameBuilder)
                    parameters.append(name)
                    canonicalSqlBuilder.append(PARAMETER_NAME_PLACEHOLDER)
                } else {
                    canonicalSqlBuilder.append(currentChar)
                    stream.moveToNextChar()
                }
            }

            return ParametrizedSql(canonicalSqlBuilder.toString(), parameters)
        }

        private fun Stream.getParameterName(builder: StringBuilder): String {
            builder.clear()
            val stream = this
            while (stream.hasNextChar()) {
                val currentChar = this.currentChar
                if (currentChar.isAllowedParameterNameCharacter()) {
                    builder.append(currentChar)
                    stream.moveToNextChar()
                } else
                    break
            }

            val name = builder.toString()
            if (name.isEmpty()) error("The parameter name is empty.")
            return name
        }

        private fun Char.isAllowedParameterNameCharacter(): Boolean =
            isLetterOrDigit() || this == '_' || this == '-'

        private fun MutableMap<String, Int>.append(name: String) {
            val previous = this.put(name, size + 1)
            if (previous != null) error("The parameter `$name` is duplicated.")
        }
    }

    private class Stream(private val value: String) {
        private var position = 0
        val currentChar: Char
            get() = value[position]

        fun moveToNextChar() {
            position += 1
        }

        fun hasNextChar(): Boolean = position < value.length
    }
}
