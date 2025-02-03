package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.ResultK.Success
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionException

public fun <ValueT, ErrorT : Any> JDBCResult<ResultRows>.mapToObject(
    mapper: ResultRowMapper<ValueT, ErrorT>
): TransactionResult<ValueT?, ErrorT, JDBCError> =
    fold(
        onSuccess = { rows ->
            val row = rows.firstOrNull()
            if (row != null) mapper(1, row) else Success.asNull
        },
        onFailure = { error -> transactionException(error) }
    )

public fun <ValueT, ErrorT : Any> JDBCResult<ResultRows>.mapToList(
    mapper: ResultRowMapper<ValueT, ErrorT>
): TransactionResult<List<ValueT>, ErrorT, JDBCError> =
    fold(
        onSuccess = { rows ->
            var index = 0
            rows.traverse { mapper.invoke(++index, it) }
        },
        onFailure = { error -> transactionException(error) }
    )
