package io.github.ustudiocompany.uframework.jdbc.connection

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCNamedPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCStatement.Timeout

/**
 * The type representing a JDBC connection to a database.
 */
public interface JBDCConnection {

    /**
     * Creates a new [JBDCPreparedStatement] instance.
     *
     * @param sql the SQL query.
     * @param timeout the timeout for the query.
     * @return a [JBDCPreparedStatement] instance or a [JDBCError] instance if the creation fails.
     */
    public fun preparedStatement(sql: String, timeout: Timeout = Timeout.Default): JDBCResult<JBDCPreparedStatement>

    /**
     * Creates a new [JBDCNamedPreparedStatement] instance.
     *
     * @param sql the prepared SQL query.
     * @param timeout the timeout for the query.
     * @return a [JBDCNamedPreparedStatement] instance or a [JDBCError] instance if the creation fails.
     */
    public fun namedPreparedStatement(
        sql: ParametrizedSql,
        timeout: Timeout = Timeout.Default
    ): JDBCResult<JBDCNamedPreparedStatement>
}
