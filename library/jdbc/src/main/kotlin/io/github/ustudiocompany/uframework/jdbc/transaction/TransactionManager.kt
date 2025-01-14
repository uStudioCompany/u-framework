package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.ustudiocompany.uframework.jdbc.connection.JdbcConnection
import io.github.ustudiocompany.uframework.jdbc.error.Incident
import javax.sql.DataSource

public fun transactionManager(dataSource: DataSource): TransactionManager =
    TransactionManagerInstance(dataSource)

public interface TransactionManager {

    public fun startTransaction(
        isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
        readOnly: Boolean = false
    ): ResultK<Transaction, Incident>
}

public inline fun <SuccessT, ErrorT> TransactionManager.useTransaction(
    isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
    block: (JdbcConnection) -> TransactionResult<SuccessT, ErrorT>
): TransactionResult<SuccessT, ErrorT> =
    startTransaction(isolation)
        .andThen { tx ->
            tx.use {
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
        }
