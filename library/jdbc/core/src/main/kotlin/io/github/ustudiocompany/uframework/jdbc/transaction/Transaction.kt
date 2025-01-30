package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.MaybeFailure
import io.github.ustudiocompany.uframework.jdbc.connection.JBDCConnection
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

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
     * @return a [JDBCError] error if the commit fails.
     */
    public fun commit(): MaybeFailure<JDBCError>

    /**
     * Rolls back the transaction.
     *
     * @return a [JDBCError] error if the rollback fails.
     */
    public fun rollback(): MaybeFailure<JDBCError>
}
