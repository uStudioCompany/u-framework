package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.ustudiocompany.uframework.jdbc.JDBCFail
import io.github.ustudiocompany.uframework.jdbc.connection.JBDCConnection

/**
 * The type representing a transaction.
 */
public interface Transaction : AutoCloseable {

    /**
     * The connection associated with this transaction.
     */
    public val connection: JBDCConnection

    /**
     * Commits the transaction.
     *
     * @return a [JDBCFail] instance if the commit fails.
     */
    public fun commit(): JDBCFail

    /**
     * Rolls back the transaction.
     *
     * @return a [JDBCFail] instance if the rollback fails.
     */
    public fun rollback(): JDBCFail
}
