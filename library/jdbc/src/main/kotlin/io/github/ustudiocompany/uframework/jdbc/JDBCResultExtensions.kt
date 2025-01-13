package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.either.Left
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.asIncident

internal inline fun <T, R, E> JDBCResult<T>.flatMapOrIncident(
    block: (T) -> TransactionResult<R, E>
): TransactionResult<R, E> =
    fold(
        onSuccess = { value -> block(value) },
        onFailure = { error -> error.asIncident() }
    )

internal inline fun <T, R, E> JDBCResult<T>.mapOrIncident(block: (T) -> ResultK<R, E>): TransactionResult<R, E> =
    flatMapOrIncident { value -> block(value).mapFailure { Left(it) } }
