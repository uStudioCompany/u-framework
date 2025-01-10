package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.map
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.generalExceptionHandling
import java.sql.Connection
import javax.sql.DataSource

internal class TransactionManagerInstance(
    private val dataSource: DataSource,
) : TransactionManager {

    override fun startTransaction(
        isolation: TransactionIsolation,
        readOnly: Boolean
    ): JDBCResult<Transaction> =
        dataSource.initConnection(isolation, readOnly)
            .map { connection -> TransactionInstance(connection) }

    private fun DataSource.initConnection(
        isolation: TransactionIsolation,
        readonly: Boolean
    ): JDBCResult<Connection> = generalExceptionHandling {
        connection.apply {
            if (isolation != TransactionIsolation.DEFAULT) transactionIsolation = isolation.get
            isReadOnly = readonly
            autoCommit = false
        }.asSuccess()
    }
}
