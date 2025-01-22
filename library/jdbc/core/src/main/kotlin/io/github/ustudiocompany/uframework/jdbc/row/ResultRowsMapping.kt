package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.ResultK.Success
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.flatMapOrIncident
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult

public fun <ValueT, ErrorT> JDBCResult<ResultRows>.mapToObject(
    mapper: ResultRowMapper<ValueT, ErrorT>
): TransactionResult<ValueT?, ErrorT> =
    flatMapOrIncident { rows ->
        val row = rows.firstOrNull()
        if (row != null) mapper(1, row) else Success.asNull
    }

public fun <ValueT, ErrorT> JDBCResult<ResultRows>.mapToList(
    mapper: ResultRowMapper<ValueT, ErrorT>
): TransactionResult<List<ValueT>, ErrorT> =
    flatMapOrIncident { rows ->
        var index = 0
        rows.traverse { mapper.invoke(++index, it) }
    }
