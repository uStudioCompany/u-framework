package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.success
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.jdbcError
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
import java.sql.PreparedStatement
import java.sql.SQLException

internal class JdbcPreparedStatementInstance(
    private val statement: PreparedStatement
) : JdbcPreparedStatement {

    override fun clearParameters() {
        statement.clearParameters()
    }

    override fun setParameter(index: Int, parameter: SqlParameter): JDBCResult<JdbcPreparedStatement> =
        trySetParameter(index) { parameter.setValue(statement, index) }

    override fun <T> setParameter(
        index: Int,
        value: T,
        setter: SqlParameterSetter<T>
    ): JDBCResult<JdbcPreparedStatement> =
        trySetParameter(index) { setter(statement, index, value) }

    override fun execute(values: Iterable<SqlParameter>): JDBCResult<StatementResult> {
        val result = statement.setParameterValues(values)
        return if (result.isFailure()) result else statement.tryExecute()
    }

    override fun query(values: Iterable<SqlParameter>): JDBCResult<ResultRows> {
        val result = statement.setParameterValues(values)
        return if (result.isFailure()) result else statement.tryExecuteQuery()
    }

    override fun update(values: Iterable<SqlParameter>): JDBCResult<Int> {
        val result = statement.setParameterValues(values)
        return if (result.isFailure()) result else statement.tryExecuteUpdate()
    }

    override fun close() {
        if (!statement.isClosed) statement.close()
    }

    private inline fun <T> trySetParameter(index: Int, block: () -> T): JDBCResult<JdbcPreparedStatement> =
        try {
            block()
            success(this)
        } catch (expected: SQLException) {
            jdbcError(
                description = "Error while setting parameter by index: '$index'.",
                exception = expected
            )
        }

    private fun PreparedStatement.setParameterValues(values: Iterable<SqlParameter>): JDBCResult<Unit> {
        values.forEachIndexed { index, parameter ->
            val paramIndex = index + 1
            val result = trySetParameter(paramIndex) {
                parameter.setValue(this, paramIndex)
            }
            if (result.isFailure()) return result
        }
        return Success.asUnit
    }
}
