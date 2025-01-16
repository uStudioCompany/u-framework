package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.map
import io.github.ustudiocompany.uframework.jdbc.JDBCFail
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JdbcConnection
import io.github.ustudiocompany.uframework.jdbc.jdbcError
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcNamedPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcNamedPreparedStatementInstance
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcPreparedStatementInstance
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcStatement
import java.sql.Connection
import java.sql.PreparedStatement

internal class TransactionInstance(
    private val unwrappedConnection: Connection,
) : Transaction, JdbcConnection {

    override val connection: JdbcConnection
        get() = this

    override fun commit(): JDBCFail = try {
        unwrappedConnection.commit()
        Success.asUnit
    } catch (expected: Exception) {
        jdbcError(
            description = "Error while committing transaction",
            exception = expected
        )
    }

    override fun rollback(): JDBCFail = try {
        unwrappedConnection.rollback()
        Success.asUnit
    } catch (expected: Exception) {
        jdbcError(
            description = "Error while rolling back transaction",
            exception = expected
        )
    }

    override fun close() {
        try {
            if (!unwrappedConnection.isClosed)
                unwrappedConnection.close()
        } catch (_: Exception) {
        }
    }

    override fun preparedStatement(
        sql: String,
        timeout: JdbcStatement.Timeout
    ): JDBCResult<JdbcPreparedStatement> =
        prepareStatement(sql, timeout)
            .map { statement -> JdbcPreparedStatementInstance(statement = statement) }

    override fun namedPreparedStatement(
        sql: ParametrizedSql,
        timeout: JdbcStatement.Timeout
    ): JDBCResult<JdbcNamedPreparedStatement> =
        prepareStatement(sql.value, timeout)
            .map { statement ->
                JdbcNamedPreparedStatementInstance(parameters = sql.parameters, statement = statement)
            }

    private fun prepareStatement(
        sql: String,
        timeout: JdbcStatement.Timeout
    ): JDBCResult<PreparedStatement> = try {
        unwrappedConnection.prepareStatement(sql)
            .apply {
                if (timeout is JdbcStatement.Timeout.Seconds) queryTimeout = timeout.value
            }
            .asSuccess()
    } catch (expected: Exception) {
        jdbcError(
            description = "Error while preparing statement",
            exception = expected
        )
    }
}
