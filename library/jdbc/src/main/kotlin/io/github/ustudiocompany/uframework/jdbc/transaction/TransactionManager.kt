package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JdbcConnection
import io.github.ustudiocompany.uframework.jdbc.use

/**
 * A transaction manager that provides a way to start a transaction.
 */
public interface TransactionManager {

    /**
     * Starts a new transaction with the given isolation level and read-only mode.
     *
     * @param isolation The transaction isolation level.
     * @param readOnly The read-only mode.
     * @return An object that allows you to manage a running transaction, or an error if the transaction cannot be
     * started.
     */
    public fun startTransaction(
        isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
        readOnly: Boolean = false
    ): JDBCResult<Transaction>
}

public inline fun <ValueT, ErrorT> TransactionManager.useTransaction(
    isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
    block: (JdbcConnection) -> TransactionResult<ValueT, ErrorT>
): TransactionResult<ValueT, ErrorT> =
    startTransaction(isolation)
        .use { tx ->
            val result = try {
                block(tx.connection)
            } catch (expected: Exception) {
                transactionIncident(
                    description = "Error while executing transaction block",
                    exception = expected
                )
            }

            if (result.isSuccess())
                tx.commit()
                    .fold(
                        onSuccess = { result },
                        onFailure = { error -> transactionIncident(error) }
                    )
            else {
                tx.rollback()
                result
            }
        }
