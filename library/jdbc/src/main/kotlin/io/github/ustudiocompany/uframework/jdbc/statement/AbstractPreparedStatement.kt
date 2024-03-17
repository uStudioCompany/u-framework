package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import io.github.ustudiocompany.uframework.jdbc.sql.param.setValue
import java.sql.Connection
import java.sql.PreparedStatement

internal abstract class AbstractPreparedStatement(
    connection: Connection,
    private val parametrizedSql: ParametrizedSql
) {

    val statement: PreparedStatement = connection.prepareStatement(parametrizedSql)

    val originalSql: String
        get() = parametrizedSql.value

    fun clearParameters() {
        statement.clearParameters()
    }

    fun PreparedStatement.setPropertyValues(values: Iterable<SqlParam>): PreparedStatement {
        values.forEach { parameter ->
            val name = parameter.name
            parametrizedSql.parameters[name]
                ?.let { position -> setValue(position, parameter) }
                ?: run {
                    val availableParameters = if (parametrizedSql.parameters.isEmpty())
                        "."
                    else
                        " (available parameters: ${parametrizedSql.parameters.keys})."
                    error("The `$name` is the unavailable parameter $availableParameters")
                }
        }
        return this
    }

    companion object {

        fun Connection.prepareStatement(sql: ParametrizedSql): PreparedStatement = try {
            prepareStatement(sql.value)
        } catch (expected: Exception) {
            throw IllegalArgumentException("The error of preparing a statement.", expected)
        }
    }
}
