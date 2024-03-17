package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import io.github.ustudiocompany.uframework.jdbc.statement.createPreparedUpdateStatement
import java.sql.Connection

public class SqlUpdate(private val sql: ParametrizedSql) {

    public fun execute(connection: Connection, vararg values: SqlParam): Result<Int, JDBCErrors> =
        execute(connection, Iterable { values.iterator() })

    public fun execute(connection: Connection, values: Iterable<SqlParam>): Result<Int, JDBCErrors> =
        connection.createPreparedUpdateStatement(sql)
            .execute(values)
}
