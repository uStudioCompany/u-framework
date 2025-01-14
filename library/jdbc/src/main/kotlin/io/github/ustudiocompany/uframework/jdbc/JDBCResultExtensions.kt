package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.fail.Fail.Companion.error
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionIncident

public inline fun <T, R, E> JDBCResult<T>.flatMapOrIncident(
    block: (T) -> TransactionResult<R, E>
): TransactionResult<R, E> =
    fold(
        onSuccess = { value -> block(value) },
        onFailure = { error -> transactionIncident(error) }
    )

public inline fun <T, R, E> JDBCResult<T>.mapOrIncident(block: (T) -> ResultK<R, E>): TransactionResult<R, E> =
    flatMapOrIncident { value -> block(value).mapFailure { error(it) } }

public fun <T> JDBCResult<T>.liftToIncident(): TransactionResult<T, Nothing> =
    if (this.isSuccess()) this else transactionIncident(this.cause)
