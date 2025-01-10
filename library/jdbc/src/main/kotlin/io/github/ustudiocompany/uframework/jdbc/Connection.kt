package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.identity
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.toFailure
import java.sql.Connection
import java.sql.SQLException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Deprecated(message = "use transaction instead", level = DeprecationLevel.WARNING)
public inline fun <T> Connection.withTransaction(
    block: Connection.() -> ResultK<T, JDBCErrors>
): ResultK<T, JDBCErrors> = withTransaction(::identity, block)

@OptIn(ExperimentalContracts::class)
@Deprecated(message = "use transaction instead", level = DeprecationLevel.WARNING)
@Suppress("TooGenericExceptionCaught")
public inline fun <T, F> Connection.withTransaction(
    noinline errorConverter: ErrorConverter<F>,
    block: Connection.() -> ResultK<T, F>
): ResultK<T, F> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return try {
        autoCommit = false
        val result = block(this)
        if (result.isSuccess())
            commit()
        else
            try {
                rollback()
            } catch (_: Throwable) {
            }
        result
    } catch (expected: SQLException) {
        expected.toFailure(errorConverter)
    } catch (expected: Throwable) {
        try {
            rollback()
        } catch (rollbackException: Throwable) {
            expected.addSuppressed(rollbackException)
        }
        errorConverter(JDBCErrors.Unexpected(expected)).asFailure()
    }
}
