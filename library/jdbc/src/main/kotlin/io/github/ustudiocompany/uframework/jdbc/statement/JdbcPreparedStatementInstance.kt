package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.Raise
import io.github.airflux.commons.types.doRaise
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.isFailure
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.onFailure
import io.github.airflux.commons.types.resultk.success
import io.github.airflux.commons.types.withRaise
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.jdbcError
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcPreparedStatement.ParametersScope
import java.sql.PreparedStatement

internal class JdbcPreparedStatementInstance(
    private val statement: PreparedStatement
) : JdbcPreparedStatement {

    override fun clearParameters() {
        statement.clearParameters()
    }

    override fun setParameter(index: Int, parameter: SqlParameter): JDBCResult<JdbcPreparedStatement> =
        trySetParameter(index) { parameter.setValue(statement, index) }.map { this }

    override fun <T> setParameter(
        index: Int,
        value: T,
        setter: SqlParameterSetter<T>
    ): JDBCResult<JdbcPreparedStatement> =
        trySetParameter(index) { setter(statement, index, value) }.map { this }

    override fun <T> setParameters(
        value: T,
        setter: ParametersScope.(T) -> Unit
    ): JDBCResult<JdbcPreparedStatement> {
        val scope = ParametersScopeInstance()
        return withRaise(scope, wrap = { error -> error.asFailure() }) {
            setter(this, value)
            success(this@JdbcPreparedStatementInstance)
        }
    }

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

    private inline fun trySetParameter(index: Int, block: () -> Unit): JDBCResult<Unit> =
        try {
            block()
            Success.asUnit
        } catch (expected: Exception) {
            jdbcError(
                description = "Error while setting parameter by index: '$index'.",
                exception = expected
            )
        }

    private inner class ParametersScopeInstance : ParametersScope, Raise<JDBCError> {

        override fun <T> set(index: Int, value: T, setter: SqlParameterSetter<T>) {
            trySetParameter(index) { setter(statement, index, value) }
                .onFailure { raise(it) }
        }

        override fun raise(error: JDBCError): Nothing {
            doRaise(error)
        }
    }
}
