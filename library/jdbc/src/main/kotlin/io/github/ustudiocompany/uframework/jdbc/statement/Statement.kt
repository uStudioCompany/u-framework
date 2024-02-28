package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.SqlType
import io.github.ustudiocompany.uframework.jdbc.sql.TypedParameter
import java.sql.Connection
import java.sql.PreparedStatement

public fun Connection.createQueryStatement(
    sql: ParametrizedSql,
    vararg parameters: Pair<String, Any>
): QueryStatement = createQueryStatement(sql, parameters.toMap())

public fun Connection.createQueryStatement(
    sql: ParametrizedSql,
    parameters: Map<String, Any>
): QueryStatement {
    val statement = createStatement(sql, parameters)
    return QueryStatement(statement)
}

public fun Connection.createInsertStatement(
    sql: ParametrizedSql,
    vararg parameters: Pair<String, Any>
): InsertStatement =
    createInsertStatement(sql, parameters.toMap())

public fun Connection.createInsertStatement(sql: ParametrizedSql, parameters: Map<String, Any>): InsertStatement {
    val statement = createStatement(sql, parameters)
    return InsertStatement(statement)
}

public fun Connection.createUpdateStatement(
    sql: ParametrizedSql,
    vararg parameters: Pair<String, Any>
): UpdateStatement =
    createUpdateStatement(sql, parameters.toMap())

public fun Connection.createUpdateStatement(sql: ParametrizedSql, parameters: Map<String, Any>): UpdateStatement {
    val statement = createStatement(sql, parameters)
    return UpdateStatement(statement)
}

public fun Connection.createStatement(sql: ParametrizedSql, parameters: Map<String, Any>): PreparedStatement {
    val prepareStatement: PreparedStatement = prepareStatement(sql)
    sql.parameters
        .forEachIndexed { index, typedParameter ->
            val position = index + 1
            val value = parameters[typedParameter.name] ?: error("The parameter `${typedParameter.name}` is not found.")
            prepareStatement.setValue(position, typedParameter, value)
        }

    return prepareStatement
}

private fun Connection.prepareStatement(sql: ParametrizedSql) = try {
    prepareStatement(sql.value)
} catch (expected: Exception) {
    throw IllegalArgumentException("The error of preparing a statement.", expected)
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
