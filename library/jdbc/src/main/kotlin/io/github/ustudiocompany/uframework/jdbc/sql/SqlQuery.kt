package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Rows
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import io.github.ustudiocompany.uframework.jdbc.statement.createPreparedQueryStatement
import java.sql.Connection

public class SqlQuery(private val sql: ParametrizedSql) {

    public fun execute(connection: Connection, vararg values: SqlParam): Result<Rows, JDBCErrors> =
        execute(connection, Iterable { values.iterator() })

    public fun execute(connection: Connection, values: Iterable<SqlParam>): Result<Rows, JDBCErrors> =
        connection.createPreparedQueryStatement(sql)
            .execute(values)
}
