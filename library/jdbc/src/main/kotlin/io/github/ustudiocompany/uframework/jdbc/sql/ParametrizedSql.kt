package io.github.ustudiocompany.uframework.jdbc.sql

public class ParametrizedSql private constructor(
    public val value: String,
    public val parameters: Map<String, Int>
) {

    public companion object {

        public fun of(sql: String): ParametrizedSql = parse(sql)

        private fun parse(value: String): ParametrizedSql {
            val parameters = mutableMapOf<String, Int>()
            val canonicalSqlBuilder = StringBuilder(value.length)
            var currentPosition = 0
            while (currentPosition < value.length) {
                val char = value[currentPosition]
                if (char == ':') {
                    val parameterNameInfo = value.getParameterName(currentPosition + 1)
                    val name = parameterNameInfo.first
                    parameters.append(name)
                    canonicalSqlBuilder.append(PARAMETER_NAME_PLACEHOLDER)
                    currentPosition = parameterNameInfo.second
                } else {
                    canonicalSqlBuilder.append(char)
                    currentPosition += 1
                }
            }

            return ParametrizedSql(canonicalSqlBuilder.toString(), parameters)
        }

        private fun MutableMap<String, Int>.append(name: String) {
            val previous = this.put(name, size + 1)
            if (previous != null) error("The parameter `$name` is duplicated.")
        }

        private fun String.getParameterName(startPosition: Int): Pair<String, Int> {
            var nextChar: Char?
            var position = startPosition
            val nameBuilder = StringBuilder()
            while (position < length) {
                nextChar = this[position]
                if (nextChar.isAllowedParameterNameCharacter()) {
                    nameBuilder.append(nextChar)
                    position += 1
                } else
                    break
            }

            val name = nameBuilder.toString()
            if (name.isEmpty()) error("The parameter name is empty.")
            return name to position
        }

        private fun Char.isAllowedParameterNameCharacter(): Boolean =
            isLetterOrDigit() || this == '_' || this == '-'

        private const val PARAMETER_NAME_PLACEHOLDER = "?"
    }
}
