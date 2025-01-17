package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.isFailure
import io.github.ustudiocompany.uframework.jdbc.JDBCFail
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.jdbcFail
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.NamedSqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
import java.sql.PreparedStatement

internal class JdbcNamedPreparedStatementInstance(
    private val parameters: Map<String, Int>,
    private val statement: PreparedStatement
) : JdbcNamedPreparedStatement {

    override fun clearParameters() {
        statement.clearParameters()
    }

    override fun setParameter(parameter: NamedSqlParameter): JDBCFail =
        trySetParameter(parameter.name) { index -> parameter.setValue(statement, index) }

    override fun <T> setParameter(name: String, value: T, setter: SqlParameterSetter<T>): JDBCFail =
        trySetParameter(name) { index -> setter(statement, index, value) }

    override fun execute(values: Iterable<NamedSqlParameter>): JDBCResult<StatementResult> {
        val result = statement.setParameterValues(values)
        return if (result.isFailure()) result else statement.tryExecute()
    }

    override fun query(values: Iterable<NamedSqlParameter>): JDBCResult<ResultRows> {
        val result = statement.setParameterValues(values)
        return if (result.isFailure()) result else statement.tryExecuteQuery()
    }

    override fun update(values: Iterable<NamedSqlParameter>): JDBCResult<Int> {
        val result = statement.setParameterValues(values)
        return if (result.isFailure()) result else statement.tryExecuteUpdate()
    }

    override fun close() {
        if (!statement.isClosed) statement.close()
    }

    private fun PreparedStatement.setParameterValues(values: Iterable<NamedSqlParameter>): JDBCFail {
        values.forEach { parameter ->
            val result = trySetParameter(parameter.name) { index ->
                parameter.setValue(this, index)
            }
            if (result.isFailure()) return result
        }
        return Success.asUnit
    }

    private inline fun trySetParameter(name: String, block: (Int) -> Unit): JDBCFail {
        val index = parameters[name]
            ?: return jdbcFail(description = "Undefined parameter with name: '$name'.")
        return try {
            block(index)
            ResultK.Success.asUnit
        } catch (expected: Exception) {
            jdbcFail(
                description = "Error while setting parameter with name: '$name' (index: '$index')",
                exception = expected
            )
        }
    }
}
