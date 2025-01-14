package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JdbcConnection
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

public interface TransactionManager {

    public fun startTransaction(
        isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
        readOnly: Boolean = false
    ): JDBCResult<Transaction>
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, ErrorT> JDBCResult<Transaction>.useTransaction(
    block: (Transaction) -> TransactionResult<ValueT, ErrorT>
): TransactionResult<ValueT, ErrorT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (isSuccess()) value.use { block(it) } else transactionIncident(this.cause)
}

public inline fun <ValueT, ErrorT> TransactionManager.useTransaction(
    isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
    block: (JdbcConnection) -> TransactionResult<ValueT, ErrorT>
): TransactionResult<ValueT, ErrorT> =
    startTransaction(isolation)
        .useTransaction { tx ->
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
