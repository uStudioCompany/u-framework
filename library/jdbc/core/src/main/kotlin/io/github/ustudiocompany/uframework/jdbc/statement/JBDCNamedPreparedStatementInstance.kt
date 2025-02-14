package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.asSome
import io.github.airflux.commons.types.maybe.fold
import io.github.airflux.commons.types.maybe.isSome
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.NamedSqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
import java.sql.PreparedStatement

internal class JBDCNamedPreparedStatementInstance(
    private val parameters: Map<String, Int>,
    private val statement: PreparedStatement
) : JBDCNamedPreparedStatement {

    override fun clearParameters() {
        statement.clearParameters()
    }

    override fun setParameter(parameter: NamedSqlParameter): Maybe<JDBCError> =
        trySetParameter(parameter.name) { index -> parameter.setValue(statement, index) }

    override fun <ValueT> setParameter(
        name: String,
        value: ValueT,
        setter: SqlParameterSetter<ValueT>
    ): Maybe<JDBCError> =
        trySetParameter(name) { index -> setter(statement, index, value) }

    override fun execute(values: Iterable<NamedSqlParameter>): JDBCResult<StatementResult> =
        statement.setParameterValues(values)
            .fold(
                onSome = { it.asFailure() },
                onNone = { statement.tryExecute() }
            )

    override fun query(values: Iterable<NamedSqlParameter>): JDBCResult<ResultRows> =
        statement.setParameterValues(values)
            .fold(
                onSome = { it.asFailure() },
                onNone = { statement.tryExecuteQuery() }
            )

    override fun update(values: Iterable<NamedSqlParameter>): JDBCResult<Int> =
        statement.setParameterValues(values)
            .fold(
                onSome = { it.asFailure() },
                onNone = { statement.tryExecuteUpdate() }
            )

    override fun close() {
        if (!statement.isClosed) statement.close()
    }

    private fun PreparedStatement.setParameterValues(values: Iterable<NamedSqlParameter>): Maybe<JDBCError> {
        values.forEach { parameter ->
            val result = trySetParameter(parameter.name) { index ->
                parameter.setValue(this, index)
            }
            if (result.isSome()) return result
        }
        return Maybe.None
    }

    private inline fun trySetParameter(name: String, block: (Int) -> Unit): Maybe<JDBCError> {
        val index = parameters[name]
            ?: return JDBCError(description = "Undefined parameter with name: '$name'.").asSome()
        return try {
            block(index)
            Maybe.None
        } catch (expected: Exception) {
            JDBCError(
                description = "Error while setting parameter with name: '$name' (index: '$index')",
                exception = expected
            ).asSome()
        }
    }
}
