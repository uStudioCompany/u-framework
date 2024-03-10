package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.statement.createPreparedInsertStatement
import java.sql.Connection

public class SqlInsert(private val sql: ParametrizedSql) {

    public fun execute(connection: Connection, vararg parameters: Pair<String, Any>): Result<Int, JDBCErrors> =
        execute(connection, parameters.toMap())

    public fun execute(connection: Connection, values: Map<String, Any>): Result<Int, JDBCErrors> =
        connection.createPreparedInsertStatement(sql)
            .execute(values)
}
