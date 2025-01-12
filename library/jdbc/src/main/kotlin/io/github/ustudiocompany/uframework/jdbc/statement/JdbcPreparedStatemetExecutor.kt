package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.generalExceptionHandling
import io.github.ustudiocompany.uframework.jdbc.row.ResultRowsInstance
import org.postgresql.util.PSQLState
import java.sql.PreparedStatement
import java.sql.SQLException

internal fun PreparedStatement.tryExecute(): JDBCResult<StatementResult> =
    generalExceptionHandling {
        try {
            val hasRows = execute()
            val result = if (hasRows)
                StatementResult.Rows(ResultRowsInstance(resultSet))
            else
                StatementResult.Count(updateCount)
            result.asSuccess()
        } catch (expected: SQLException) {
            if (expected.isSyntaxError)
                JDBCErrors.Statement.InvalidSql(expected).asFailure()
            else if (expected.isParameterNotSpecified)
                JDBCErrors.Statement.ParameterNotSpecified(expected).asFailure()
            else
                throw expected
        }
    }

internal fun PreparedStatement.tryExecuteQuery() = generalExceptionHandling {
    try {
        val result = executeQuery()
        ResultRowsInstance(result).asSuccess()
    } catch (expected: SQLException) {
        if (expected.isSyntaxError)
            JDBCErrors.Statement.InvalidSql(expected).asFailure()
        else if (expected.isParameterNotSpecified)
            JDBCErrors.Statement.ParameterNotSpecified(expected).asFailure()
        else if (expected.isNoResult)
            JDBCErrors.Statement.NoResult(expected).asFailure()
        else
            throw expected
    }
}

internal fun PreparedStatement.tryExecuteUpdate(): JDBCResult<Int> = generalExceptionHandling {
    try {
        val result = executeUpdate()
        result.asSuccess()
    } catch (expected: SQLException) {
        if (expected.isSyntaxError)
            JDBCErrors.Statement.InvalidSql(expected).asFailure()
        else if (expected.isParameterNotSpecified)
            JDBCErrors.Statement.ParameterNotSpecified(expected).asFailure()
        else if (expected.isUnexpectedResult)
            JDBCErrors.Statement.UnexpectedResult(expected).asFailure()
        else
            throw expected
    }
}

internal val SQLException.isSyntaxError: Boolean
    get() = sqlState == PSQLState.SYNTAX_ERROR.state

internal val SQLException.isParameterNotSpecified: Boolean
    get() = sqlState == PSQLState.INVALID_PARAMETER_VALUE.state

internal val SQLException.isInvalidParameterIndex: Boolean
    get() = sqlState == PSQLState.INVALID_PARAMETER_VALUE.state

internal val SQLException.isNoResult: Boolean
    get() = sqlState == PSQLState.NO_DATA.state

internal val SQLException.isUnexpectedResult: Boolean
    get() = sqlState == PSQLState.TOO_MANY_RESULTS.state
