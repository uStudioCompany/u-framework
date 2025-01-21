package io.github.ustudiocompany.uframework.jdbc.connection

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcNamedPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcStatement.Timeout

/**
 * The type representing a JDBC connection to a database.
 */
public interface JdbcConnection {

    /**
     * Creates a new [JdbcPreparedStatement] instance.
     *
     * @param sql the SQL query.
     * @param timeout the timeout for the query.
     * @return a [JdbcPreparedStatement] instance or a [JDBCError] instance if the creation fails.
     */
    public fun preparedStatement(sql: String, timeout: Timeout = Timeout.Default): JDBCResult<JdbcPreparedStatement>

    /**
     * Creates a new [JdbcNamedPreparedStatement] instance.
     *
     * @param sql the prepared SQL query.
     * @param timeout the timeout for the query.
     * @return a [JdbcNamedPreparedStatement] instance or a [JDBCError] instance if the creation fails.
     */
    public fun namedPreparedStatement(
        sql: ParametrizedSql,
        timeout: Timeout = Timeout.Default
    ): JDBCResult<JdbcNamedPreparedStatement>
}
