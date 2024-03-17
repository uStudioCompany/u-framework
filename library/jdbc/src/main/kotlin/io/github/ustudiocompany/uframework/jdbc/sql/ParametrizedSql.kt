package io.github.ustudiocompany.uframework.jdbc.sql

public class ParametrizedSql private constructor(
    public val value: String,
    public val parameters: Map<String, Int>
) {

    public companion object {

        public fun of(sql: String): ParametrizedSql {
            val parameters = sql.getParameters()
            val canonicalSql = sql.getCanonicalSql()
            return ParametrizedSql(canonicalSql, parameters)
        }

        private fun String.getParameters(): Map<String, Int> {
            val sql = this
            return mutableMapOf<String, Int>()
                .apply {
                    pattern.findAll(sql)
                        .forEachIndexed { index, result ->
                            val name = result.groups[0]!!.value.substring(1) //REFACTORED
                            val previous = put(name, index + 1)
                            if (previous != null) error("The parameter `$name` is duplicated.")
                        }
                }
        }

        private fun String.getCanonicalSql(): String = pattern.replace(this, "?")

        private val pattern = """:[a-zA-z]+[a-zA-z0-9-_]*""".toRegex()
    }
}
