package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.SqlType
import io.github.ustudiocompany.uframework.jdbc.sql.TypedParameter
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

    fun PreparedStatement.setPropertyValues(values: Map<String, Any>): PreparedStatement {
        parametrizedSql.parameters
            .forEachIndexed { index, typedParameter ->
                val position = index + 1
                val value = values[typedParameter.name]
                    ?: error("The parameter `${typedParameter.name}` is not found.")
                setValue(position, typedParameter, value)
            }
        return this
    }

    private fun PreparedStatement.setValue(position: Int, typedParameter: TypedParameter, value: Any) =
        when (typedParameter.type) {
            SqlType.BOOLEAN -> if (value is Boolean)
                setBoolean(position, value)
            else
                error("The parameter `${typedParameter.name}` is not `Boolean` type.")

            SqlType.TEXT -> if (value is String)
                setString(position, value)
            else
                error("The parameter `${typedParameter.name}` is not `String` type.")

            SqlType.INTEGER -> if (value is Int)
                setInt(position, value)
            else
                error("The parameter `${typedParameter.name}` is not `Integer` type.")

            SqlType.BIGINT -> if (value is Long)
                setLong(position, value)
            else
                error("The parameter `${typedParameter.name}` is not `Bigint` type.")
        }

    companion object {

        fun Connection.prepareStatement(sql: ParametrizedSql): PreparedStatement = try {
            prepareStatement(sql.value)
        } catch (expected: Exception) {
            throw IllegalArgumentException("The error of preparing a statement.", expected)
        }
    }
}
