package io.github.ustudiocompany.uframework.jdbc

import io.github.airflux.commons.types.fail.Fail.Companion.error
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.isSuccess
import io.github.airflux.commons.types.resultk.mapFailure
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionIncident

public inline fun <SuccessT, SuccessR, ErrorT> JDBCResult<SuccessT>.flatMapOrIncident(
    block: (SuccessT) -> TransactionResult<SuccessR, ErrorT>
): TransactionResult<SuccessR, ErrorT> =
    fold(
        onSuccess = { value -> block(value) },
        onFailure = { error -> transactionIncident(error) }
    )

public inline fun <SuccessT, SuccessR, ErrorT> JDBCResult<SuccessT>.mapOrIncident(
    block: (SuccessT) -> ResultK<SuccessR, ErrorT>
): TransactionResult<SuccessR, ErrorT> =
    flatMapOrIncident { value -> block(value).mapFailure { error(it) } }

public fun <SuccessT> JDBCResult<SuccessT>.liftToIncident(): TransactionResult<SuccessT, Nothing> =
    if (this.isSuccess()) this else transactionIncident(this.cause)
