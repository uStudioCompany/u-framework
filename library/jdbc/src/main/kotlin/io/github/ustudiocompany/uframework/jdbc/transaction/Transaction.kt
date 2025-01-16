package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.ustudiocompany.uframework.jdbc.JDBCFail
import io.github.ustudiocompany.uframework.jdbc.connection.JdbcConnection

public interface Transaction : AutoCloseable {
    public val connection: JdbcConnection

    public fun commit(): JDBCFail
    public fun rollback(): JDBCFail
}
