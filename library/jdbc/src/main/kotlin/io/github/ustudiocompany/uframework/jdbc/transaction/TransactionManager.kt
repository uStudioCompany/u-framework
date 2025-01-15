package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JdbcConnection
import io.github.ustudiocompany.uframework.jdbc.use

public interface TransactionManager {

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
