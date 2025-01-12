package io.github.ustudiocompany.uframework.jdbc.connection

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcNamedPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcStatement.Timeout

public interface JdbcConnection {

    public fun preparedStatement(
        sql: String,
        timeout: Timeout = Timeout.Default
    ): JDBCResult<JdbcPreparedStatement>

    public fun namedPreparedStatement(
        sql: ParametrizedSql,
        timeout: Timeout = Timeout.Default
    ): JDBCResult<JdbcNamedPreparedStatement>
}
