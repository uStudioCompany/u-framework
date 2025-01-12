package io.github.ustudiocompany.uframework.jdbc.exception

import io.github.airflux.commons.types.identity
import io.github.airflux.commons.types.resultk.Failure
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.error.TransactionError
import java.sql.SQLException

public fun Exception.toFailure(): Failure<JDBCErrors> = toFailure(::identity)

public fun <F> Exception.toFailure(errorConverter: ErrorConverter<F>): Failure<F> {
    val error = if (this is SQLException)
        when {
            this.isConnectionError -> JDBCErrors.Connection(this)
            this.isDuplicate -> JDBCErrors.Data.DuplicateKeyValue(this)
            this.isCustom -> JDBCErrors.Custom(this.sqlState, this)
            else -> JDBCErrors.Unexpected(this)
        }
    else
        JDBCErrors.Unexpected(this)
    return errorConverter(error).asFailure()
}

public fun Exception.toTransactionError(): Failure<TransactionError> {
    val error = if (this is SQLException)
        when {
            this.isConnectionError -> TransactionError.Connection(this)
            this.isDuplicate -> TransactionError.Data.DuplicateKeyValue(this)
            else -> TransactionError.Unexpected(this)
        }
    else
        TransactionError.Unexpected(this)
    return error.asFailure()
}
