package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.map
import io.github.ustudiocompany.uframework.jdbc.JDBCFail
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JBDCConnection
import io.github.ustudiocompany.uframework.jdbc.jdbcFail
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCNamedPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCNamedPreparedStatementInstance
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCPreparedStatementInstance
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCStatement
import java.sql.Connection
import java.sql.PreparedStatement

internal class TransactionInstance(
    private val unwrappedConnection: Connection,
) : Transaction, JBDCConnection {

    override val connection: JBDCConnection
        get() = this

    override fun commit(): JDBCFail = try {
        unwrappedConnection.commit()
        Success.asUnit
    } catch (expected: Exception) {
        jdbcFail(description = "Error while committing transaction", exception = expected)
    }

    override fun rollback(): JDBCFail = try {
        unwrappedConnection.rollback()
        Success.asUnit
    } catch (expected: Exception) {
        jdbcFail(description = "Error while rolling back transaction", exception = expected)
    }

    override fun close() {
        try {
            if (!unwrappedConnection.isClosed)
                unwrappedConnection.close()
        } catch (_: Exception) {
            // ignore
        }
    }

    override fun preparedStatement(
        sql: String,
        timeout: JBDCStatement.Timeout
    ): JDBCResult<JBDCPreparedStatement> =
        prepareStatement(sql, timeout)
            .map { statement -> JBDCPreparedStatementInstance(statement = statement) }

    override fun namedPreparedStatement(
        sql: ParametrizedSql,
        timeout: JBDCStatement.Timeout
    ): JDBCResult<JBDCNamedPreparedStatement> =
        prepareStatement(sql.value, timeout)
            .map { statement ->
                JBDCNamedPreparedStatementInstance(parameters = sql.parameters, statement = statement)
            }

    private fun prepareStatement(
        sql: String,
        timeout: JBDCStatement.Timeout
    ): JDBCResult<PreparedStatement> = try {
        unwrappedConnection.prepareStatement(sql)
            .apply {
                if (timeout is JBDCStatement.Timeout.Seconds) queryTimeout = timeout.value
            }
            .asSuccess()
    } catch (expected: Exception) {
        jdbcFail(
            description = "Error while preparing statement",
            exception = expected
        )
    }
}
