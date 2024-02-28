package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.statement.createInsertStatement
import java.sql.Connection

public class SqlInsert(private val sql: ParametrizedSql) {

    public fun execute(connection: Connection, vararg parameters: Pair<String, Any>): Result<Boolean, JDBCErrors> =
        execute(connection, parameters.toMap())

    public fun execute(connection: Connection, parameters: Map<String, Any>): Result<Boolean, JDBCErrors> =
        connection.createInsertStatement(sql, parameters.toMap())
            .execute()
}
