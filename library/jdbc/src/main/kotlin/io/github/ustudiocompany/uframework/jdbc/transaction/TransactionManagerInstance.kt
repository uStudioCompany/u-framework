package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.Incident
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.mapOrIncident
import java.sql.Connection
import javax.sql.DataSource

internal class TransactionManagerInstance(
    private val dataSource: DataSource,
) : TransactionManager {

    override fun startTransaction(
        isolation: TransactionIsolation,
        readOnly: Boolean
    ): ResultK<Transaction, Incident> =
        dataSource.initConnection(isolation, readOnly)
            .mapOrIncident { connection ->
                TransactionInstance(connection).asSuccess()
            }

    private fun DataSource.initConnection(
        isolation: TransactionIsolation,
        readonly: Boolean
    ): JDBCResult<Connection> = try {
        connection.apply {
            if (isolation != TransactionIsolation.DEFAULT) transactionIsolation = isolation.get
            isReadOnly = readonly
            autoCommit = false
        }.asSuccess()
    } catch (expected: Exception) {
        JDBCError(
            description = "Error while initializing transaction.",
            exception = expected
        ).asFailure()
    }
}
