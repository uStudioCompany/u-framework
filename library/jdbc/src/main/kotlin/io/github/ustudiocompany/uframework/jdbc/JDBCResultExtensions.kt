package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.either.Left
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.jdbc.error.asIncident
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult

public inline fun <T, R, E> JDBCResult<T>.flatMapOrIncident(
    block: (T) -> TransactionResult<R, E>
): TransactionResult<R, E> =
    fold(
        onSuccess = { value -> block(value) },
        onFailure = { error -> error.asIncident() }
    )

public inline fun <T, R, E> JDBCResult<T>.mapOrIncident(block: (T) -> ResultK<R, E>): TransactionResult<R, E> =
    flatMapOrIncident { value -> block(value).mapFailure { Left(it) } }

public fun <T> JDBCResult<T>.liftToIncident(): TransactionResult<T, Nothing> =
    if (this.isSuccess()) this else this.cause.asIncident()
