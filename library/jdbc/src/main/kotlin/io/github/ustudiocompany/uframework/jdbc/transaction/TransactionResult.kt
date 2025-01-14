package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.Fail.Companion.error
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.error.incident

public typealias TransactionResult<SuccessT, ErrorT> = ResultK<SuccessT, Fail<ErrorT, JDBCError>>
public typealias TransactionError<ErrorT> = TransactionResult<Nothing, ErrorT>
public typealias TransactionIncident = TransactionResult<Nothing, Nothing>

public fun <ErrorT> transactionError(error: ErrorT): TransactionError<ErrorT> = error(error).asFailure()

public fun transactionIncident(description: String, exception: Throwable? = null): TransactionIncident =
    transactionIncident(JDBCError(description, exception))

public fun transactionIncident(error: JDBCError): TransactionIncident = incident(error).asFailure()
