package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.identity
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import java.sql.Connection
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public inline fun <T> Connection.withTransaction(
    block: Connection.() -> ResultK<T, JDBCErrors>
): ResultK<T, JDBCErrors> = withTransaction(::identity, block)

@OptIn(ExperimentalContracts::class)
@Suppress("TooGenericExceptionCaught")
public inline fun <T, F> Connection.withTransaction(
    errorConverter: ErrorConverter<F>,
    block: Connection.() -> ResultK<T, F>
): ResultK<T, F> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return try {
        autoCommit = false
        val result = block(this)
        commit()
        result
    } catch (commitException: Throwable) {
        try {
            rollback()
        } catch (rollbackException: Throwable) {
            commitException.addSuppressed(rollbackException)
        }
        errorConverter(JDBCErrors.UnexpectedError(commitException)).asFailure()
    }
}
