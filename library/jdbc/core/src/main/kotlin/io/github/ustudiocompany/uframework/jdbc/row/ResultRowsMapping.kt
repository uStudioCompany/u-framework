package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.BiFailureResultK
import io.github.airflux.commons.types.resultk.ResultK.Success
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionException

public fun <ValueT, ErrorT : Any, ExceptionT : Any> ResultRows.mapToObject(
    mapper: ResultRowMapper<ValueT, ErrorT, ExceptionT>
): BiFailureResultK<ValueT?, ErrorT, ExceptionT> {
    val row = this.firstOrNull()
    return if (row != null) mapper(1, row) else Success.asNull
}

public fun <ValueT, ErrorT : Any> JDBCResult<ResultRows>.mapToObject(
    mapper: ResultRowMapper<ValueT, ErrorT, JDBCError>
): BiFailureResultK<ValueT?, ErrorT, JDBCError> =
    fold(
        onSuccess = { rows -> rows.mapToObject(mapper) },
        onFailure = { error -> transactionException(error) }
    )

public fun <ValueT, ErrorT : Any, ExceptionT : Any> ResultRows.mapToList(
    mapper: ResultRowMapper<ValueT, ErrorT, ExceptionT>
): BiFailureResultK<List<ValueT>, ErrorT, ExceptionT> {
    var index = 0
    return this.traverse { mapper.invoke(++index, it) }
}

public fun <ValueT, ErrorT : Any> JDBCResult<ResultRows>.mapToList(
    mapper: ResultRowMapper<ValueT, ErrorT, JDBCError>
): BiFailureResultK<List<ValueT>, ErrorT, JDBCError> =
    fold(
        onSuccess = { rows -> rows.mapToList(mapper) },
        onFailure = { error -> transactionException(error) }
    )
