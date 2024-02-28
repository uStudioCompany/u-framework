package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Rows
import io.github.ustudiocompany.uframework.jdbc.statement.createQueryStatement
import java.sql.Connection

public class SqlQuery(private val sql: ParametrizedSql) {

    public fun execute(connection: Connection, vararg parameters: Pair<String, Any>): Result<Rows, JDBCErrors> =
        execute(connection, parameters.toMap())

    public fun execute(connection: Connection, parameters: Map<String, Any>): Result<Rows, JDBCErrors> =
        connection.createQueryStatement(sql, parameters.toMap())
            .execute()
}
