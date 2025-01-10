package io.github.ustudiocompany.uframework.jdbc.connection

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcPreparedStatement.Timeout

public interface JdbcConnection {

    public fun preparedStatement(
        sql: String,
        timeout: Timeout = Timeout.Default
    ): JDBCResult<JdbcPreparedStatement>
}
