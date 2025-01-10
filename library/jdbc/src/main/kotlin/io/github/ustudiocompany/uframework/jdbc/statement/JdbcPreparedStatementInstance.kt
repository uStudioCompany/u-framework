package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.generalExceptionHandling
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.row.ResultRowsInstance
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
import org.postgresql.util.PSQLState
import java.sql.PreparedStatement
import java.sql.SQLException

internal class JdbcPreparedStatementInstance(
    private val statement: PreparedStatement
) : JdbcPreparedStatement {

    override fun clearParameters() {
        statement.clearParameters()
    }

    override fun setParameter(index: Int, parameter: SqlParameter): JDBCResult<Unit> =
        setParameter(index) {
            parameter.setValue(statement, index)
        }

    override fun <T> setParameter(
        index: Int,
        value: T,
        setter: SqlParameterSetter<T>
    ): JDBCResult<Unit> = setParameter(index) {
        setter(statement, index, value)
    }

    override fun execute(values: Iterable<SqlParameter>): JDBCResult<StatementResult> =
        generalExceptionHandling {
            statement.setParameterValues(values)
            val hasRows = statement.execute()
            val result = if (hasRows)
                StatementResult.Rows(ResultRowsInstance(statement.resultSet))
            else
                StatementResult.Count(statement.updateCount)
            result.asSuccess()
        }

    override fun query(values: Iterable<SqlParameter>): JDBCResult<ResultRows> = generalExceptionHandling {
        try {
            statement.setParameterValues(values)
            val result = statement.executeQuery()
            ResultRowsInstance(result).asSuccess()
        } catch (expected: SQLException) {
            if (expected.isParameterNotSpecified)
                JDBCErrors.Statement.ParameterNotSpecified(expected).asFailure()
            else if (expected.isNoResult)
                JDBCErrors.Statement.NoResult(expected).asFailure()
            else
                throw expected
        }
    }

    override fun update(values: Iterable<SqlParameter>): JDBCResult<Int> = generalExceptionHandling {
        try {
            statement.setParameterValues(values)
            val result = statement.executeUpdate()
            result.asSuccess()
        } catch (expected: SQLException) {
            if (expected.isParameterNotSpecified)
                JDBCErrors.Statement.ParameterNotSpecified(expected).asFailure()
            else if (expected.isUnexpectedResult)
                JDBCErrors.Statement.UnexpectedResult(expected).asFailure()
            else
                throw expected
        }
    }

    override fun close() {
        if (!statement.isClosed) statement.close()
    }

    private fun <T> setParameter(index: Int, block: () -> T) = generalExceptionHandling {
        try {
            block()
            Success.asUnit
        } catch (expected: SQLException) {
            if (expected.isInvalidParameterIndex)
                JDBCErrors.Statement.InvalidParameter(index, expected).asFailure()
            else
                throw expected
        }
    }

    private fun PreparedStatement.setParameterValues(values: Iterable<SqlParameter>) =
        values.forEachIndexed { index, parameter ->
            parameter.setValue(this, index + 1)
        }

    private val SQLException.isParameterNotSpecified: Boolean
        get() = sqlState == PSQLState.INVALID_PARAMETER_VALUE.state

    private val SQLException.isInvalidParameterIndex: Boolean
        get() = sqlState == PSQLState.INVALID_PARAMETER_VALUE.state

    private val SQLException.isNoResult: Boolean
        get() = sqlState == PSQLState.NO_DATA.state

    private val SQLException.isUnexpectedResult: Boolean
        get() = sqlState == PSQLState.TOO_MANY_RESULTS.state
}
