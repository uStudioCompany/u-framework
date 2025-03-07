package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.commons.types.identity
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.fold
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import java.sql.Connection

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public fun <T, F : Any> rowMappingQuery(
    sql: ParametrizedSql,
    errorConverter: ErrorConverter<F>,
    mapper: (Row) -> ResultK<T, F>
): RowMappingQuery<T, F> = RowMappingQuery(sql, errorConverter, mapper)

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public fun <T> rowMappingQuery(
    sql: ParametrizedSql,
    mapper: (Row) -> ResultK<T, JDBCErrors>
): RowMappingQuery<T, JDBCErrors> = RowMappingQuery(sql, ::identity, mapper)

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public class RowMappingQuery<out T, out F : Any>(
    sql: ParametrizedSql,
    private val errorConverter: ErrorConverter<F>,
    private val mapper: (Row) -> ResultK<T, F>
) {
    private val query = SqlQuery(sql)

    @Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
    public fun execute(connection: Connection, vararg parameters: SqlParam): ResultK<T?, F> =
        execute(connection, Iterable { parameters.iterator() })

    @Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
    public fun execute(connection: Connection, parameters: Iterable<SqlParam>): ResultK<T?, F> =
        query.execute(connection, parameters)
            .fold(
                onSuccess = { rows ->
                    rows.firstOrNull()
                        ?.let { row -> mapper(row) }
                        ?: Success.asNull
                },
                onFailure = { errorConverter(it).asFailure() }
            )
}
