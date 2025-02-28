package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.fold
import io.github.airflux.commons.types.maybe.isSome
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
import java.sql.PreparedStatement

internal class JDBCPreparedStatementInstance(
    private val statement: PreparedStatement
) : JDBCPreparedStatement {

    override fun clearParameters() {
        statement.clearParameters()
    }

    override fun setParameter(index: Int, parameter: SqlParameter): Maybe<JDBCError> =
        trySetParameter(index) { parameter.setValue(statement, index) }

    override fun <ValueT> setParameter(
        index: Int,
        value: ValueT,
        setter: SqlParameterSetter<ValueT>
    ): Maybe<JDBCError> =
        trySetParameter(index) { setter(statement, index, value) }

    override fun execute(values: Iterable<SqlParameter>): JDBCResult<StatementResult> =
        statement.setParameterValues(values)
            .fold(
                onSome = { it.asFailure() },
                onNone = { statement.tryExecute() }
            )

    override fun query(values: Iterable<SqlParameter>): JDBCResult<ResultRows> =
        statement.setParameterValues(values)
            .fold(
                onSome = { it.asFailure() },
                onNone = { statement.tryExecuteQuery() }
            )

    override fun update(values: Iterable<SqlParameter>): JDBCResult<Int> =
        statement.setParameterValues(values)
            .fold(
                onSome = { it.asFailure() },
                onNone = { statement.tryExecuteUpdate() }
            )

    override fun close() {
        if (!statement.isClosed) statement.close()
    }

    private fun PreparedStatement.setParameterValues(values: Iterable<SqlParameter>): Maybe<JDBCError> {
        values.forEachIndexed { index, parameter ->
            val paramIndex = index + 1
            val result = trySetParameter(paramIndex) {
                parameter.setValue(this, paramIndex)
            }
            if (result.isSome()) return result
        }
        return Maybe.None
    }

    private inline fun trySetParameter(index: Int, block: () -> Unit): Maybe<JDBCError> =
        Maybe.catch(
            catch = { exception ->
                JDBCError(description = "Error while setting parameter by index: '$index'.", exception = exception)
            },
            block = block
        )
}
