package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.row.Rows
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

public fun Connection.createPreparedQueryStatement(sql: ParametrizedSql): PreparedQueryStatement =
    PreparedQueryStatementImpl(this, sql)

public interface PreparedQueryStatement {
    public val originalSql: String
    public fun clearParameters()
    public fun execute(vararg values: SqlParam): Result<Rows, JDBCErrors> = execute(Iterable { values.iterator() })
    public fun execute(values: Iterable<SqlParam>): Result<Rows, JDBCErrors>
}

private class PreparedQueryStatementImpl(
    connection: Connection,
    parametrizedSql: ParametrizedSql
) : PreparedQueryStatement,
    AbstractPreparedStatement(connection, parametrizedSql) {

    override fun execute(values: Iterable<SqlParam>): Result<Rows, JDBCErrors> = try {
        statement.execute(values).success()
    } catch (expected: SQLException) {
        val error = if (expected.isConnectionError)
            JDBCErrors.Connection(expected)
        else
            JDBCErrors.UnexpectedError(expected)
        error.failure()
    } catch (expected: Exception) {
        JDBCErrors.UnexpectedError(expected).failure()
    }

    private fun PreparedStatement.execute(values: Iterable<SqlParam>): Rows =
        Rows(setPropertyValues(values).executeQuery())
}
