package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.airflux.functional.flatMap
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.Connection

public class RowMappingQuery<out T>(sql: ParametrizedSql, private val mapper: (Row) -> Result<T, JDBCErrors>) {
    private val query = SqlQuery(sql)

    public fun execute(connection: Connection, vararg parameters: Pair<String, Any>): Result<T?, JDBCErrors> =
        execute(connection, parameters.toMap())

    public fun execute(connection: Connection, parameters: Map<String, Any>): Result<T?, JDBCErrors> =
        query.execute(connection, parameters)
            .flatMap {
                it.firstOrNull()
                    ?.let { row -> mapper(row) }
                    ?: Result.asNull
            }
}