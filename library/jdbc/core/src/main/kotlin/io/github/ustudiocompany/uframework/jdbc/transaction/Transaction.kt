package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.maybe.Maybe
import io.github.ustudiocompany.uframework.jdbc.connection.JDBCConnection
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

/**
 * The type representing a transaction.
 */
public interface Transaction : AutoCloseable {

    /**
     * The connection associated with this transaction.
     */
    public val connection: JDBCConnection

    /**
     * Commits the transaction.
     *
     * @return a [JDBCError] error if the commit fails.
     */
    public fun commit(): Maybe<JDBCError>

    /**
     * Rolls back the transaction.
     *
     * @return a [JDBCError] error if the rollback fails.
     */
    public fun rollback(): Maybe<JDBCError>
}
