package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.either.Either
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.map
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import java.sql.Connection
import javax.sql.DataSource

internal class TransactionManagerInstance(
    private val dataSource: DataSource,
) : TransactionManager {

    override fun startTransaction(
        isolation: TransactionIsolation,
        readOnly: Boolean
    ): ResultK<Transaction, Either<Nothing, JDBCError>> =
        dataSource.initConnection(isolation, readOnly)
            .map { connection -> TransactionInstance(connection) }

    private fun DataSource.initConnection(
        isolation: TransactionIsolation,
        readonly: Boolean
    ): ResultK<Connection, Either<Nothing, JDBCError>> = try {
        connection.apply {
            if (isolation != TransactionIsolation.DEFAULT) transactionIsolation = isolation.get
            isReadOnly = readonly
            autoCommit = false
        }.asSuccess()
    } catch (expected: Exception) {
        transactionError(
            description = "Error while initializing transaction.",
            exception = expected
        )
    }
}
