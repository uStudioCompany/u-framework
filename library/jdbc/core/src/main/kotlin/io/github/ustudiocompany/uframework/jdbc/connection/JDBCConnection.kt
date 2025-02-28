package io.github.ustudiocompany.uframework.jdbc.connection

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.statement.JDBCNamedPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JDBCPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JDBCStatement.Timeout

/**
 * The type representing a JDBC connection to a database.
 */
public interface JDBCConnection {

    /**
     * Creates a new [JDBCPreparedStatement] instance.
     *
     * @param sql the SQL query.
     * @param timeout the timeout for the query.
     * @return a [JDBCPreparedStatement] instance or a [JDBCError] instance if the creation fails.
     */
    public fun preparedStatement(sql: String, timeout: Timeout = Timeout.Default): JDBCResult<JDBCPreparedStatement>

    /**
     * Creates a new [JDBCNamedPreparedStatement] instance.
     *
     * @param sql the prepared SQL query.
     * @param timeout the timeout for the query.
     * @return a [JDBCNamedPreparedStatement] instance or a [JDBCError] instance if the creation fails.
     */
    public fun namedPreparedStatement(
        sql: ParametrizedSql,
        timeout: Timeout = Timeout.Default
    ): JDBCResult<JDBCNamedPreparedStatement>
}
