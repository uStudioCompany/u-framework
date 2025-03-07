package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.fail.Fail.Companion.error
import io.github.airflux.commons.types.fail.exception
import io.github.airflux.commons.types.resultk.BiFailureResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError

/**
 * Represents the result of performing operations within a transaction.
 *
 * @param ValueT the type of the value.
 * @param ErrorT the type of the error.
 */
public typealias TransactionResult<ValueT, ErrorT, ExceptionT> = BiFailureResultK<ValueT, ErrorT, ExceptionT>

public typealias TransactionError<ErrorT> = BiFailureResultK<Nothing, ErrorT, Nothing>

public typealias TransactionException<ExceptionT> = BiFailureResultK<Nothing, Nothing, ExceptionT>

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
public fun transactionException(description: String, exception: Throwable? = null): TransactionException<JDBCError> =
    transactionException(JDBCError(description, exception))

public fun <ExceptionT : Any> transactionException(error: ExceptionT): TransactionException<ExceptionT> =
    exception(error).asFailure()
