package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.Fail
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.error.error
import io.github.ustudiocompany.uframework.jdbc.error.incident

public typealias TransactionResult<T, E> = ResultK<T, Fail<E, JDBCError>>
public typealias TransactionError<E> = TransactionResult<Nothing, E>
public typealias TransactionIncident = TransactionResult<Nothing, Nothing>

public fun <ErrorT> transactionError(error: ErrorT): TransactionError<ErrorT> = error(error).asFailure()

public fun transactionIncident(description: String, exception: Throwable? = null): TransactionIncident =
    transactionIncident(JDBCError(description, exception))

public fun transactionIncident(error: JDBCError): TransactionIncident = incident(error).asFailure()
