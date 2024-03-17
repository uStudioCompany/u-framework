package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import io.github.ustudiocompany.uframework.jdbc.statement.createPreparedInsertStatement
import java.sql.Connection

public class SqlInsert(private val sql: ParametrizedSql) {

    public fun execute(connection: Connection, vararg parameters: SqlParam): Result<Int, JDBCErrors> =
        execute(connection, Iterable { parameters.iterator() })

    public fun execute(connection: Connection, values: Iterable<SqlParam>): Result<Int, JDBCErrors> =
        connection.createPreparedInsertStatement(sql)
            .execute(values)
}
