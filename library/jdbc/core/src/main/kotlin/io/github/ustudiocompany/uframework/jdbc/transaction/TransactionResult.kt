package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.fail.Fail
import io.github.airflux.commons.types.fail.Fail.Companion.error
import io.github.airflux.commons.types.fail.exception
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

/**
 * Represents the result of performing operations within a transaction.
 *
 * @param ValueT the type of the value.
 * @param ErrorT the type of the error.
 */
public typealias TransactionResult<ValueT, ErrorT> = ResultK<ValueT, Fail<ErrorT, JDBCError>>

public typealias TransactionError<ErrorT> = ResultK<Nothing, Fail<ErrorT, JDBCError>>

public typealias TransactionException = ResultK<Nothing, Fail<Nothing, JDBCError>>

/**
 * Creates a domain (business) error related to the operations within the transaction.
 *
 * @param ErrorT the type of the error.
 * @param error The error value.
 */
public fun <ErrorT : Any> transactionError(error: ErrorT): TransactionError<ErrorT> = error(error).asFailure()

/**
 * Creates a technical error related to the transaction.
 */
public fun transactionException(description: String, exception: Throwable? = null): TransactionException =
    transactionException(JDBCError(description, exception))

public fun transactionException(error: JDBCError): TransactionException = exception(error).asFailure()
