package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.flatMap
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.connection.JdbcConnection
import io.github.ustudiocompany.uframework.jdbc.jdbcError
import javax.sql.DataSource

public fun transactionManager(dataSource: DataSource): TransactionManager =
    TransactionManagerInstance(dataSource)

public interface TransactionManager {

    public fun startTransaction(
        isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
        readOnly: Boolean = false
    ): JDBCResult<Transaction>
}

public fun <T> TransactionManager.useTransaction(
    isolation: TransactionIsolation = TransactionIsolation.READ_COMMITTED,
    block: (JdbcConnection) -> JDBCResult<T>
): JDBCResult<T> =
    startTransaction(isolation)
        .andThen { tx ->
            tx.use {
                val result: JDBCResult<T> = try {
                    block(tx.connection)
                } catch (expected: Exception) {
                    jdbcError(
                        description = "Error while executing transaction block",
                        exception = expected
                    )
                }

                if (result.isSuccess())
                    tx.commit().flatMap { result }
                else {
                    tx.rollback()
                    result
                }
            }
        }
