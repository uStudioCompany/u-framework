package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.toFailure
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
    public fun execute(vararg values: SqlParam): ResultK<Int, JDBCErrors> = execute(Iterable { values.iterator() })
    public fun execute(values: Iterable<SqlParam>): ResultK<Int, JDBCErrors>
}

private class PreparedUpdateStatementImpl(
    connection: Connection,
    parametrizedSql: ParametrizedSql
) : PreparedUpdateStatement,
    AbstractPreparedStatement(connection, parametrizedSql) {

    override fun execute(values: Iterable<SqlParam>): ResultK<Int, JDBCErrors> = try {
        statement.execute(values).asSuccess()
    } catch (expected: SQLException) {
        expected.toFailure()
    } catch (expected: Exception) {
        JDBCErrors.Unexpected(expected).asFailure()
    }

    private fun PreparedStatement.execute(values: Iterable<SqlParam>): Int = setPropertyValues(values).executeUpdate()
}
