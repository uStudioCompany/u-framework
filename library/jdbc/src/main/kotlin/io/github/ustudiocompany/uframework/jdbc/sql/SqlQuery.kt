package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Rows
import io.github.ustudiocompany.uframework.jdbc.statement.createPreparedQueryStatement
import java.sql.Connection

public class SqlQuery(private val sql: ParametrizedSql) {

    public fun execute(connection: Connection, vararg values: Pair<String, Any>): Result<Rows, JDBCErrors> =
        execute(connection, values.toMap())

    public fun execute(connection: Connection, values: Map<String, Any>): Result<Rows, JDBCErrors> =
        connection.createPreparedQueryStatement(sql)
            .execute(values)
}
