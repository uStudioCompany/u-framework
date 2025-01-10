package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.map
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JdbcConnection
import io.github.ustudiocompany.uframework.jdbc.generalExceptionHandling
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcPreparedStatementInstance
import java.sql.Connection
import java.sql.PreparedStatement

internal class TransactionInstance(
    private val unwrappedConnection: Connection,
) : Transaction, JdbcConnection {

    override val connection: JdbcConnection
        get() = this

    override fun commit(): JDBCResult<Unit> = generalExceptionHandling {
        unwrappedConnection.commit()
        Success.asUnit
    }

    override fun rollback(): JDBCResult<Unit> = generalExceptionHandling {
        unwrappedConnection.rollback()
        Success.asUnit
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
        timeout: JdbcPreparedStatement.Timeout
    ): JDBCResult<JdbcPreparedStatement> =
        prepareStatement(sql, timeout)
            .map { statement -> JdbcPreparedStatementInstance(statement = statement) }

    private fun prepareStatement(
        sql: String,
        timeout: JdbcPreparedStatement.Timeout
    ): JDBCResult<PreparedStatement> = generalExceptionHandling {
        unwrappedConnection.prepareStatement(sql)
            .apply {
                if (timeout is JdbcPreparedStatement.Timeout.Seconds) queryTimeout = timeout.value
            }
            .asSuccess()
    }
}
