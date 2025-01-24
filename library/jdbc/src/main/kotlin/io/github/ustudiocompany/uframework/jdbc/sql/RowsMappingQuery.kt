package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.commons.types.identity
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.fold
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import java.sql.Connection

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public fun <T, F : Any> rowsMappingQuery(
    sql: ParametrizedSql,
    errorConverter: ErrorConverter<F>,
    mapper: (Row) -> ResultK<T, F>
): RowsMappingQuery<T, F> = RowsMappingQuery(sql, errorConverter, mapper)

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public fun <T> rowsMappingQuery(
    sql: ParametrizedSql,
    mapper: (Row) -> ResultK<T, JDBCErrors>
): RowsMappingQuery<T, JDBCErrors> = RowsMappingQuery(sql, ::identity, mapper)

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public class RowsMappingQuery<out T, out F : Any>(
    sql: ParametrizedSql,
    private val errorConverter: ErrorConverter<F>,
    private val mapper: (Row) -> ResultK<T, F>
) {
    private val query = SqlQuery(sql)

    @Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
    public fun execute(connection: Connection, vararg parameters: SqlParam): ResultK<List<T>, F> =
        execute(connection, Iterable { parameters.iterator() })

    @Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
    public fun execute(connection: Connection, parameters: Iterable<SqlParam>): ResultK<List<T>, F> =
        query.execute(connection, parameters)
            .fold(
                onSuccess = { rows -> rows.traverse(mapper) },
                onFailure = { errorConverter(it).asFailure() }
            )
}
