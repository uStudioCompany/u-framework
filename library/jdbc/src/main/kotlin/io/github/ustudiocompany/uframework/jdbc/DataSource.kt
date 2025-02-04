package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.identity
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import java.sql.Connection
import javax.sql.DataSource
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Deprecated(message = "use transaction instead", level = DeprecationLevel.WARNING)
public inline fun <T> DataSource.useConnection(block: (Connection) -> ResultK<T, JDBCErrors>): ResultK<T, JDBCErrors> =
    useConnection(::identity, block)

@OptIn(ExperimentalContracts::class)
@Deprecated(message = "use transaction instead", level = DeprecationLevel.WARNING)
public inline fun <T, F : Any> DataSource.useConnection(
    errorConverter: ErrorConverter<F>,
    block: (Connection) -> ResultK<T, F>
): ResultK<T, F> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return try {
        connection.use(block)
    } catch (expected: Exception) {
        errorConverter(JDBCErrors.Unexpected(expected)).asFailure()
    }
}
