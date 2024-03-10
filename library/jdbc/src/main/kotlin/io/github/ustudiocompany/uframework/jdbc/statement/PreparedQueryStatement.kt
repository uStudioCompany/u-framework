package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.row.Rows
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

public fun Connection.createPreparedQueryStatement(sql: ParametrizedSql): PreparedQueryStatement =
    PreparedQueryStatementImpl(this, sql)

public interface PreparedQueryStatement {
    public val originalSql: String
    public fun clearParameters()
    public fun execute(values: Map<String, Any>): Result<Rows, JDBCErrors>
}

private class PreparedQueryStatementImpl(
    connection: Connection,
    parametrizedSql: ParametrizedSql
) : PreparedQueryStatement,
    AbstractPreparedStatement(connection, parametrizedSql) {

    override fun execute(values: Map<String, Any>): Result<Rows, JDBCErrors> = try {
        statement.execute(values).success()
    } catch (expected: SQLException) {
        val error = if (expected.isConnectionError)
            JDBCErrors.Connection(expected)
        else
            JDBCErrors.UnexpectedError(expected)
        error.error()
    } catch (expected: Exception) {
        JDBCErrors.UnexpectedError(expected).error()
    }

    private fun PreparedStatement.execute(values: Map<String, Any>): Rows =
        Rows(setPropertyValues(values).executeQuery())
}
