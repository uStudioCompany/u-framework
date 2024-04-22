package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.commons.types.identity
import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.fold
import io.github.airflux.commons.types.result.traverse
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import java.sql.Connection

public fun <T, F> rowsMappingQuery(
    sql: ParametrizedSql,
    errorConverter: ErrorConverter<F>,
    mapper: (Row) -> Result<T, F>
): RowsMappingQuery<T, F> = RowsMappingQuery(sql, errorConverter, mapper)

public fun <T> rowsMappingQuery(
    sql: ParametrizedSql,
    mapper: (Row) -> Result<T, JDBCErrors>
): RowsMappingQuery<T, JDBCErrors> = RowsMappingQuery(sql, ::identity, mapper)

public class RowsMappingQuery<out T, out F>(
    sql: ParametrizedSql,
    private val errorConverter: ErrorConverter<F>,
    private val mapper: (Row) -> Result<T, F>
) {
    private val query = SqlQuery(sql)

    public fun execute(connection: Connection, vararg parameters: SqlParam): Result<List<T>, F> =
        execute(connection, Iterable { parameters.iterator() })

    public fun execute(connection: Connection, parameters: Iterable<SqlParam>): Result<List<T>, F> =
        query.execute(connection, parameters)
            .fold(
                onSuccess = { rows -> rows.traverse(mapper) },
                onFailure = { errorConverter(it).failure() }
            )
}
