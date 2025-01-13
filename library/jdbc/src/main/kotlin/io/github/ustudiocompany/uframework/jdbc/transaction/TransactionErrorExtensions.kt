package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.either.Right
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

@PublishedApi
internal fun transactionError(description: String, exception: Throwable? = null): TransactionError =
    transactionError(JDBCError(description, exception))

@PublishedApi
internal fun transactionError(error: JDBCError): TransactionError = Right(error).asFailure()
