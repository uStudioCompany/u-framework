package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.identity
import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import java.sql.Connection
import javax.sql.DataSource
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public inline fun <T> DataSource.useConnection(block: (Connection) -> Result<T, JDBCErrors>): Result<T, JDBCErrors> =
    useConnection(::identity, block)

@OptIn(ExperimentalContracts::class)
public inline fun <T, F> DataSource.useConnection(
    errorConverter: ErrorConverter<F>,
    block: (Connection) -> Result<T, F>
): Result<T, F> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return try {
        connection.use(block)
    } catch (expected: Exception) {
        errorConverter(JDBCErrors.UnexpectedError(expected)).failure()
    }
}
