package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JdbcConnection

public interface Transaction : AutoCloseable {
    public val connection: JdbcConnection

    public fun commit(): JDBCResult<Unit>
    public fun rollback(): JDBCResult<Unit>
}
