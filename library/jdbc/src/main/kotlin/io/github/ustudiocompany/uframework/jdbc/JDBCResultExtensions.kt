package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.fail.Fail.Companion.error
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionIncident
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

public inline fun <T, ValueT, ErrorT> JDBCResult<T>.flatMapOrIncident(
    block: (T) -> TransactionResult<ValueT, ErrorT>
): TransactionResult<ValueT, ErrorT> =
    fold(
        onSuccess = { value -> block(value) },
        onFailure = { error -> transactionIncident(error) }
    )

public inline fun <T, ValueT, ErrorT> JDBCResult<T>.mapOrIncident(
    block: (T) -> ResultK<ValueT, ErrorT>
): TransactionResult<ValueT, ErrorT> =
    flatMapOrIncident { value -> block(value).mapFailure { error(it) } }

public fun <T> JDBCResult<T>.liftToIncident(): TransactionResult<T, Nothing> =
    if (this.isSuccess()) this else transactionIncident(this.cause)

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT, ErrorT, T : AutoCloseable> JDBCResult<T>.use(
    block: (T) -> TransactionResult<ValueT, ErrorT>
): TransactionResult<ValueT, ErrorT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (this.isSuccess())
        this.value.use { block(it) }
    else
        transactionIncident(this.cause)
}
