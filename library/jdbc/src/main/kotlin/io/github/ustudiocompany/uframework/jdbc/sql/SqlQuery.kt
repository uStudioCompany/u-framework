package io.github.ustudiocompany.uframework.jdbc.sql

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Rows
import io.github.ustudiocompany.uframework.jdbc.sql.param.SqlParam
import io.github.ustudiocompany.uframework.jdbc.statement.createPreparedQueryStatement
import java.sql.Connection

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public class SqlQuery(private val sql: ParametrizedSql) {

    @Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
    public fun execute(connection: Connection, vararg values: SqlParam): ResultK<Rows, JDBCErrors> =
        execute(connection, Iterable { values.iterator() })

    @Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
    public fun execute(connection: Connection, values: Iterable<SqlParam>): ResultK<Rows, JDBCErrors> =
        connection.createPreparedQueryStatement(sql)
            .execute(values)
}
