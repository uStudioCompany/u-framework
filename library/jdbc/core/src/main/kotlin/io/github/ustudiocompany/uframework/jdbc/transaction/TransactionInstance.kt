@file:Suppress("ImportOrdering")

package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.onNone
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.map
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JDBCConnection
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.statement.JDBCNamedPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JDBCNamedPreparedStatementInstance
import io.github.ustudiocompany.uframework.jdbc.statement.JDBCPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JDBCPreparedStatementInstance
import io.github.ustudiocompany.uframework.jdbc.statement.JDBCStatement
import io.github.ustudiocompany.uframework.telemetry.logging.logger.slf4jextension.debug
import io.github.ustudiocompany.uframework.telemetry.logging.logger.slf4jextension.error
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import org.slf4j.LoggerFactory

internal class TransactionInstance(
    private val unwrappedConnection: Connection,
) : Transaction, JDBCConnection {

    private val logger = LoggerFactory.getLogger(TransactionInstance::class.java)

    override val connection: JDBCConnection
        get() = this

    override fun commit(): Maybe<JDBCError> {
        logger.debug("Commit transaction started.")
        return Maybe.catch(
            block = { unwrappedConnection.commit() },
            catch = { exception ->
                JDBCError(description = "Error while committing transaction.", exception = exception)
            }
        ).onNone { logger.debug("Commit transaction finished.") }
    }

    override fun rollback(): Maybe<JDBCError> {
        logger.debug("Rollback transaction started.")
        return Maybe.catch(
            block = { unwrappedConnection.rollback() },
            catch = { exception ->
                JDBCError(description = "Error while rolling back transaction.", exception = exception)
            }
        ).onNone { logger.debug("Rollback transaction finished.") }
    }

    override fun close() {
        logger.debug("Closing transaction connection.")
        try {
            if (!unwrappedConnection.isClosed)
                unwrappedConnection.close()
        } catch (e: SQLException) {
            logger.error(e) { "Error occurred while closing connection." }
        }
        logger.debug("Transaction connection closed.")
    }

    override fun preparedStatement(
        sql: String,
        timeout: JDBCStatement.Timeout
    ): JDBCResult<JDBCPreparedStatement> {
        logger.debug { "Executing Query: \n $sql" }
        return prepareStatement(sql, timeout)
            .map { statement -> JDBCPreparedStatementInstance(statement = statement) }
    }

    override fun namedPreparedStatement(
        sql: ParametrizedSql,
        timeout: JDBCStatement.Timeout
    ): JDBCResult<JDBCNamedPreparedStatement> {
        logger.debug { "Executing ParametrizedSql: \n $sql" }
        return prepareStatement(sql.value, timeout)
            .map { statement ->
                JDBCNamedPreparedStatementInstance(parameters = sql.parameters, statement = statement)
            }
    }

    private fun prepareStatement(
        sql: String,
        timeout: JDBCStatement.Timeout
    ): JDBCResult<PreparedStatement> = ResultK.catch(
        catch = { exception -> JDBCError(description = "Error while preparing statement", exception = exception) },
        block = {
            unwrappedConnection.prepareStatement(sql)
                .apply {
                    if (timeout is JDBCStatement.Timeout.Seconds) queryTimeout = timeout.value
                }
        }
    )
}
