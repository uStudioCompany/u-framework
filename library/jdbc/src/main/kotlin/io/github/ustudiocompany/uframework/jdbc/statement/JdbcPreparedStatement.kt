package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter

public interface JdbcPreparedStatement : JdbcStatement {

    public fun clearParameters()

    public fun setParameter(index: Int, param: SqlParameter): JDBCResult<JdbcPreparedStatement>

    public fun <T> setParameter(
        index: Int,
        value: T,
        setter: SqlParameterSetter<T>
    ): JDBCResult<JdbcPreparedStatement>

    public fun execute(vararg values: SqlParameter): JDBCResult<StatementResult> =
        execute(Iterable { values.iterator() })

    public fun execute(values: Iterable<SqlParameter>): JDBCResult<StatementResult>

    public fun query(vararg values: SqlParameter): JDBCResult<ResultRows> = query(Iterable { values.iterator() })

    public fun query(values: Iterable<SqlParameter>): JDBCResult<ResultRows>

    public fun update(vararg values: SqlParameter): JDBCResult<Int> = update(Iterable { values.iterator() })

    public fun update(values: Iterable<SqlParameter>): JDBCResult<Int>
}
