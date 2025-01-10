package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter

public fun <T> JdbcPreparedStatement.queryForObject(
    vararg parameters: SqlParameter,
    mapper: RowMapper<T>
): JDBCResult<T?> =
    queryForObject(Iterable { parameters.iterator() }, mapper)

public fun <T> JdbcPreparedStatement.queryForObject(
    parameters: Iterable<SqlParameter>,
    mapper: RowMapper<T>
): JDBCResult<T?> =
    query(parameters)
        .andThen { rows ->
            val row = rows.firstOrNull()
            if (row != null) mapper(1, row) else Success.asNull
        }

public fun <T> JdbcPreparedStatement.queryForList(
    vararg parameters: SqlParameter,
    mapper: RowMapper<T>
): JDBCResult<List<T>> =
    queryForList(Iterable { parameters.iterator() }, mapper)

public fun <T> JdbcPreparedStatement.queryForList(
    parameters: Iterable<SqlParameter>,
    mapper: RowMapper<T>
): JDBCResult<List<T>> =
    query(parameters)
        .andThen { rows ->
            var index = 0
            rows.traverse { mapper.invoke(++index, it) }
        }
