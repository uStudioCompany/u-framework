package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.fold
import io.github.airflux.functional.identity
import io.github.ustudiocompany.uframework.jdbc.error.ErrorConverter
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.Connection

public fun <T, F> rowMappingQuery(
    sql: ParametrizedSql,
    errorConverter: ErrorConverter<F>,
    mapper: (Row) -> Result<T, F>
): RowMappingQuery<T, F> = RowMappingQuery(sql, errorConverter, mapper)

public fun <T> rowMappingQuery(
    sql: ParametrizedSql,
    mapper: (Row) -> Result<T, JDBCErrors>
): RowMappingQuery<T, JDBCErrors> = RowMappingQuery(sql, ::identity, mapper)

public class RowMappingQuery<out T, out F>(
    sql: ParametrizedSql,
    private val errorConverter: ErrorConverter<F>,
    private val mapper: (Row) -> Result<T, F>
) {
    private val query = SqlQuery(sql)

    public fun execute(connection: Connection, vararg parameters: Pair<String, Any>): Result<T?, F> =
        execute(connection, parameters.toMap())

    public fun execute(connection: Connection, parameters: Map<String, Any>): Result<T?, F> =
        query.execute(connection, parameters)
            .fold(
                onSuccess = { rows ->
                    rows.firstOrNull()
                        ?.let { row -> mapper(row) }
                        ?: Result.asNull
                },
                onError = { errorConverter(it).error() }
            )
}
