package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

public fun Connection.createPreparedUpdateStatement(sql: ParametrizedSql): PreparedUpdateStatement =
    PreparedUpdateStatementImpl(this, sql)

public interface PreparedUpdateStatement {
    public val originalSql: String
    public fun clearParameters()
    public fun execute(vararg values: SqlParam): Result<Int, JDBCErrors> = execute(Iterable { values.iterator() })
    public fun execute(values: Iterable<SqlParam>): Result<Int, JDBCErrors>
}

private class PreparedUpdateStatementImpl(
    connection: Connection,
    parametrizedSql: ParametrizedSql
) : PreparedUpdateStatement,
    AbstractPreparedStatement(connection, parametrizedSql) {

    override fun execute(values: Iterable<SqlParam>): Result<Int, JDBCErrors> = try {
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

    private fun PreparedStatement.execute(values: Iterable<SqlParam>): Int = setPropertyValues(values).executeUpdate()
}
