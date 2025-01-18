package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.Fail.Companion.error
import io.github.airflux.commons.types.fail.exception
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

public typealias TransactionResult<ValueT, ErrorT> = ResultK<ValueT, Fail<ErrorT, JDBCError>>

public fun <ErrorT> transactionError(error: ErrorT): TransactionResult<Nothing, ErrorT> = error(error).asFailure()

public fun transactionIncident(
    description: String,
    exception: Throwable? = null
): TransactionResult<Nothing, Nothing> =
    transactionIncident(JDBCError(description, exception))

public fun transactionIncident(error: JDBCError): TransactionResult<Nothing, Nothing> =
    exception(error).asFailure()
