package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.flatMapOrIncident
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult

public fun <ValueT, ErrorT> JdbcPreparedStatement.queryForObject(
    vararg parameters: SqlParameter,
    mapper: RowMapper<ValueT, ErrorT>
): TransactionResult<ValueT?, ErrorT> =
    queryForObject(Iterable { parameters.iterator() }, mapper)

public fun <ValueT, ErrorT> JdbcPreparedStatement.queryForObject(
    parameters: Iterable<SqlParameter>,
    mapper: RowMapper<ValueT, ErrorT>
): TransactionResult<ValueT?, ErrorT> =
    query(parameters)
        .flatMapOrIncident { rows ->
            val row = rows.firstOrNull()
            if (row != null) mapper(1, row) else Success.asNull
        }

public fun <ValueT, ErrorT> JdbcPreparedStatement.queryForList(
    vararg parameters: SqlParameter,
    mapper: RowMapper<ValueT, ErrorT>
): TransactionResult<List<ValueT>, ErrorT> =
    queryForList(Iterable { parameters.iterator() }, mapper)

public fun <ValueT, ErrorT> JdbcPreparedStatement.queryForList(
    parameters: Iterable<SqlParameter>,
    mapper: RowMapper<ValueT, ErrorT>
): TransactionResult<List<ValueT>, ErrorT> =
    query(parameters)
        .flatMapOrIncident { rows ->
            var index = 0
            rows.traverse { mapper.invoke(++index, it) }
        }
