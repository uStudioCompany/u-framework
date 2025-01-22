package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.jdbcFail
import io.github.ustudiocompany.uframework.jdbc.row.ResultRowsInstance
import java.sql.PreparedStatement

internal fun PreparedStatement.tryExecute(): JDBCResult<StatementResult> = try {
    val hasRows = execute()
    val result = if (hasRows)
        StatementResult.Rows(ResultRowsInstance(resultSet))
    else
        StatementResult.Count(updateCount)
    result.asSuccess()
} catch (expected: Exception) {
    jdbcFail(description = "Error while executing the statement.", exception = expected)
}

internal fun PreparedStatement.tryExecuteQuery() = try {
    val result = executeQuery()
    ResultRowsInstance(result).asSuccess()
} catch (expected: Exception) {
    jdbcFail(description = "Error while executing the query.", exception = expected)
}

internal fun PreparedStatement.tryExecuteUpdate(): JDBCResult<Int> = try {
    val result = executeUpdate()
    result.asSuccess()
} catch (expected: Exception) {
    jdbcFail(description = "Error while executing the update.", exception = expected)
}
