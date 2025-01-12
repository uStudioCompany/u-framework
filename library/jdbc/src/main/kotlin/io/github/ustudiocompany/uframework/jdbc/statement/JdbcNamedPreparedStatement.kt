package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.NamedSqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter

public interface JdbcNamedPreparedStatement : JdbcStatement {

    public fun clearParameters()

    public fun setParameter(param: NamedSqlParameter): JDBCResult<Unit>

    public fun <T> setParameter(
        name: String,
        value: T,
        setter: SqlParameterSetter<T>
    ): JDBCResult<Unit>

    public fun execute(vararg values: NamedSqlParameter): JDBCResult<StatementResult> =
        execute(Iterable { values.iterator() })

    public fun execute(values: Iterable<NamedSqlParameter>): JDBCResult<StatementResult>

    public fun query(vararg values: NamedSqlParameter): JDBCResult<ResultRows> = query(Iterable { values.iterator() })

    public fun query(values: Iterable<NamedSqlParameter>): JDBCResult<ResultRows>

    public fun update(vararg values: NamedSqlParameter): JDBCResult<Int> = update(Iterable { values.iterator() })

    public fun update(values: Iterable<NamedSqlParameter>): JDBCResult<Int>
}
