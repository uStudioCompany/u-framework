package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.statement.createUpdateStatement
import java.sql.Connection

public class SqlUpdate(private val sql: ParametrizedSql) {

    public fun execute(connection: Connection, vararg parameters: Pair<String, Any>): Result<Boolean, JDBCErrors> =
        execute(connection, parameters.toMap())

    public fun execute(connection: Connection, parameters: Map<String, Any>): Result<Boolean, JDBCErrors> =
        connection.createUpdateStatement(sql, parameters.toMap())
            .execute()
}
