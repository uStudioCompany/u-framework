package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.airflux.functional.flatMap
import io.github.airflux.functional.traverse
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import java.sql.Connection

public class RowsMappingQuery<out T>(sql: ParametrizedSql, private val mapper: (Row) -> Result<T, JDBCErrors>) {
    private val query = SqlQuery(sql)

    public fun execute(connection: Connection, vararg parameters: SqlParam): Result<List<T>, JDBCErrors> =
        execute(connection, Iterable { parameters.iterator() })

    public fun execute(connection: Connection, parameters: Iterable<SqlParam>): Result<List<T>, JDBCErrors> =
        query.execute(connection, parameters)
            .flatMap { it.traverse(mapper) }
}
