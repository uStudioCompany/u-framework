package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.airflux.commons.types.resultk.liftToError
import io.github.airflux.commons.types.resultk.liftToException
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

public fun <ValueT> JDBCResult<ValueT>.liftToTransactionException(): TransactionResult<ValueT, Nothing> =
    this.liftToException()

public fun <ValueT, ErrorT : Any> ResultK<ValueT, ErrorT>.liftToTransactionError(): TransactionResult<ValueT, ErrorT> =
    this.liftToError()

@OptIn(ExperimentalContracts::class)
public inline infix fun <ValueT : AutoCloseable, ValueR, ErrorT : Any> JDBCResult<ValueT>.use(
    block: (ValueT) -> TransactionResult<ValueR, ErrorT>
): TransactionResult<ValueR, ErrorT> {
    contract {
        callsInPlace(block, AT_MOST_ONCE)
    }
    return if (this.isSuccess())
        this.value.use { block(it) }
    else
        transactionException(this.cause)
}
