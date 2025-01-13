package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.flatMapOrIncident
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult

public fun <T, E> JdbcPreparedStatement.queryForObject(
    vararg parameters: SqlParameter,
    mapper: RowMapper<T, E>
): TransactionResult<T?, E> =
    queryForObject(Iterable { parameters.iterator() }, mapper)

public fun <T, E> JdbcPreparedStatement.queryForObject(
    parameters: Iterable<SqlParameter>,
    mapper: RowMapper<T, E>
): TransactionResult<T?, E> =
    query(parameters)
        .flatMapOrIncident { rows ->
            val row = rows.firstOrNull()
            if (row != null) mapper(1, row) else Success.asNull
        }

public fun <T, E> JdbcPreparedStatement.queryForList(
    vararg parameters: SqlParameter,
    mapper: RowMapper<T, E>
): TransactionResult<List<T>, E> =
    queryForList(Iterable { parameters.iterator() }, mapper)

public fun <T, E> JdbcPreparedStatement.queryForList(
    parameters: Iterable<SqlParameter>,
    mapper: RowMapper<T, E>
): TransactionResult<List<T>, E> =
    query(parameters)
        .flatMapOrIncident { rows ->
            var index = 0
            rows.traverse { mapper.invoke(++index, it) }
        }
