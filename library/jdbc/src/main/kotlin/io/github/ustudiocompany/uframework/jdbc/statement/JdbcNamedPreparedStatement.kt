package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.NamedSqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter

public interface JdbcNamedPreparedStatement : JdbcStatement {

    public fun clearParameters()

    public fun setParameter(param: NamedSqlParameter): JDBCResult<JdbcNamedPreparedStatement>

    public fun <T> setParameter(
        name: String,
        value: T,
        setter: SqlParameterSetter<T>
    ): JDBCResult<JdbcNamedPreparedStatement>

    public fun <T> setParameters(
        value: T,
        setter: ParametersScope.(T) -> Unit
    ): JDBCResult<JdbcNamedPreparedStatement>

    public fun execute(vararg values: NamedSqlParameter): JDBCResult<StatementResult> = execute(values.asIterable())

    public fun execute(values: Iterable<NamedSqlParameter>): JDBCResult<StatementResult>

    public fun query(vararg values: NamedSqlParameter): JDBCResult<ResultRows> = query(values.asIterable())

    public fun query(values: Iterable<NamedSqlParameter>): JDBCResult<ResultRows>

    public fun update(vararg values: NamedSqlParameter): JDBCResult<Int> = update(values.asIterable())

    public fun update(values: Iterable<NamedSqlParameter>): JDBCResult<Int>

    public interface ParametersScope {
        public fun <T> set(name: String, value: T, setter: SqlParameterSetter<T>)
    }
}
