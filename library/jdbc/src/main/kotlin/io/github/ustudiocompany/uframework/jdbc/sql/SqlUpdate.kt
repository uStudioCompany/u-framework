package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.statement.createPreparedUpdateStatement
import java.sql.Connection

public class SqlUpdate(private val sql: ParametrizedSql) {

    public fun execute(connection: Connection, vararg values: Pair<String, Any>): Result<Int, JDBCErrors> =
        execute(connection, values.toMap())

    public fun execute(connection: Connection, values: Map<String, Any>): Result<Int, JDBCErrors> =
        connection.createPreparedUpdateStatement(sql)
            .execute(values)
}
