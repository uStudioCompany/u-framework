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
import io.github.ustudiocompany.uframework.telemetry.logging.logger.slf4jextension.error
import io.github.ustudiocompany.uframework.telemetry.logging.logger.slf4jextension.trace
import io.github.ustudiocompany.uframework.telemetry.logging.logger.slf4jextension.warn
import java.sql.Connection
import java.sql.PreparedStatement
import org.slf4j.LoggerFactory

internal class TransactionInstance(
    private val unwrappedConnection: Connection,
) : Transaction, JDBCConnection {

    private val logger = LoggerFactory.getLogger(TransactionInstance::class.java)

    override val connection: JDBCConnection
        get() = this

    override fun commit(): Maybe<JDBCError> {
        logger.trace { "Commit transaction started." }
        return Maybe.catch(
            block = { unwrappedConnection.commit() },
            catch = { exception ->
                JDBCError(description = "Error while committing transaction.", exception = exception)
            }
        ).onNone { logger.trace { "Commit transaction finished." } }
    }

    override fun rollback(): Maybe<JDBCError> {
        logger.warn { "Rollback transaction started." }
        return Maybe.catch(
            block = { unwrappedConnection.rollback() },
            catch = { exception ->
                JDBCError(description = "Error while rolling back transaction.", exception = exception)
            }
        ).onNone { logger.trace { "Rollback transaction finished." } }
    }

    override fun close() {
        logger.trace { "Closing transaction connection." }
        try {
            if (!unwrappedConnection.isClosed)
                unwrappedConnection.close()
        } catch (expected: Exception) {
            logger.error(expected) { "Error occurred while closing connection." }
        }
        logger.trace { "Transaction connection closed." }
    }

    override fun preparedStatement(
        sql: String,
        timeout: JDBCStatement.Timeout
    ): JDBCResult<JDBCPreparedStatement> {
        logger.trace { "Executing Query: $sql" }
        return prepareStatement(sql, timeout)
            .map { statement -> JDBCPreparedStatementInstance(statement = statement) }
    }

    override fun namedPreparedStatement(
        sql: ParametrizedSql,
        timeout: JDBCStatement.Timeout
    ): JDBCResult<JDBCNamedPreparedStatement> {
        logger.trace { "Executing ParametrizedSql: ${sql.value.trim()}" }
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
