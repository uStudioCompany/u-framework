package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.toFailure
import io.github.ustudiocompany.uframework.jdbc.row.Rows
import io.github.ustudiocompany.uframework.jdbc.row.RowsInstance
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
    public fun execute(vararg values: SqlParam): ResultK<Rows, JDBCErrors> = execute(Iterable { values.iterator() })
    public fun execute(values: Iterable<SqlParam>): ResultK<Rows, JDBCErrors>
}

private class PreparedQueryStatementImpl(
    connection: Connection,
    parametrizedSql: ParametrizedSql
) : PreparedQueryStatement,
    AbstractPreparedStatement(connection, parametrizedSql) {

    override fun execute(values: Iterable<SqlParam>): ResultK<Rows, JDBCErrors> = try {
        statement.execute(values).asSuccess()
    } catch (expected: SQLException) {
        expected.toFailure()
    } catch (expected: Exception) {
        JDBCErrors.Unexpected(expected).asFailure()
    }

    private fun PreparedStatement.execute(values: Iterable<SqlParam>): Rows =
        RowsInstance(setPropertyValues(values).executeQuery())
}
