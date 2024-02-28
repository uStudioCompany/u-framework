package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import java.sql.Connection
import javax.sql.DataSource
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public inline fun <T> DataSource.useConnection(
    block: (Connection) -> Result<T, JDBCErrors>
): Result<T, JDBCErrors> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return try {
        connection.use(block)
    } catch (expected: Exception) {
        JDBCErrors.UnexpectedError(expected).error()
    }
}
