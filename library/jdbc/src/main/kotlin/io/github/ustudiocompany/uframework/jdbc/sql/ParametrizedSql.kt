package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.ustudiocompany.uframework.jdbc.sql.internal.TypedParameterResolver

public class ParametrizedSql private constructor(
    public val value: String,
    public val parameters: List<TypedParameter>
) {

    public companion object {

        public fun of(sql: String, vararg parameters: Pair<String, SqlType>): ParametrizedSql =
            of(sql, parameters.toMap())

        public fun of(sql: String, parameters: Map<String, SqlType>): ParametrizedSql {
            val typedParameters = sql.getTypedParameters(parameters)
            val canonicalSql = pattern.replace(sql, "?")
            return ParametrizedSql(canonicalSql, typedParameters)
        }

        private fun String.getTypedParameters(parameters: Map<String, SqlType>): List<TypedParameter> {
            val typedParameterResolver = TypedParameterResolver.of(parameters)
            val typedParameters = pattern.findAll(this)
                .map { result ->
                    val name = result.groups[0]!!.value
                    typedParameterResolver.resolve(name)
                }
                .toList()
            if (parameters.size != typedParameters.size)
                error(
                    "The number of passed parameters differs from the number of parameters in the SQL" +
                        "(passed: ${parameters.size} [${parameters.map { it.key }.joinToString()}], " +
                        "parameters in SQL: ${typedParameters.size} [${typedParameters.joinToString { it.name }}])."
                )
            return typedParameters
        }

        private val pattern = """:[a-zA-z]+[a-zA-z0-9-_]*""".toRegex()
    }
}
