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

public inline fun <ValueT, ValueR, ErrorT> JDBCResult<ValueT>.flatMapOrIncident(
    block: (ValueT) -> TransactionResult<ValueR, ErrorT>
): TransactionResult<ValueR, ErrorT> =
    fold(
        onSuccess = { value -> block(value) },
        onFailure = { error -> transactionIncident(error) }
    )

public inline fun <ValueT, ValueR, ErrorT> JDBCResult<ValueT>.mapOrIncident(
    block: (ValueT) -> ResultK<ValueR, ErrorT>
): TransactionResult<ValueR, ErrorT> =
    flatMapOrIncident { value -> block(value).mapFailure { error(it) } }

public fun <ValueT> JDBCResult<ValueT>.liftToTransactionResult(): TransactionResult<ValueT, Nothing> =
    if (this.isSuccess()) this else transactionIncident(this.cause)

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT : AutoCloseable, ValueR, ErrorT> JDBCResult<ValueT>.use(
    block: (ValueT) -> TransactionResult<ValueR, ErrorT>
): TransactionResult<ValueR, ErrorT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (this.isSuccess())
        this.value.use { block(it) }
    else
        transactionIncident(this.cause)
}
