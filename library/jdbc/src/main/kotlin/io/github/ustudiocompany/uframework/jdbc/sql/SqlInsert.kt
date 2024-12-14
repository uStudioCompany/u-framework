package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import io.github.ustudiocompany.uframework.jdbc.statement.createPreparedInsertStatement
import java.sql.Connection

public class SqlInsert(private val sql: ParametrizedSql) {

    public fun execute(connection: Connection, vararg parameters: SqlParam): ResultK<Int, JDBCErrors> =
        execute(connection, Iterable { parameters.iterator() })

    public fun execute(connection: Connection, values: Iterable<SqlParam>): ResultK<Int, JDBCErrors> =
        connection.createPreparedInsertStatement(sql)
            .execute(values)
}
