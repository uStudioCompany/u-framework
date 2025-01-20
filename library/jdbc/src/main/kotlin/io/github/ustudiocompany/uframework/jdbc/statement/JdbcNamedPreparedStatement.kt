package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.Raise
import io.github.airflux.commons.types.doRaise
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.onFailure
import io.github.airflux.commons.types.resultk.success
import io.github.airflux.commons.types.withRaise
import io.github.ustudiocompany.uframework.jdbc.JDBCFail
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.NamedSqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcNamedPreparedStatement.ParametersScope

public interface JdbcNamedPreparedStatement : JdbcStatement {

    public fun clearParameters()

    public fun setParameter(param: NamedSqlParameter): JDBCFail

    public fun <ValueT> setParameter(name: String, value: ValueT, setter: SqlParameterSetter<ValueT>): JDBCFail

    public fun execute(vararg values: NamedSqlParameter): JDBCResult<StatementResult> = execute(values.asIterable())

    public fun execute(values: Iterable<NamedSqlParameter>): JDBCResult<StatementResult>

    public fun query(vararg values: NamedSqlParameter): JDBCResult<ResultRows> = query(values.asIterable())

    public fun query(values: Iterable<NamedSqlParameter>): JDBCResult<ResultRows>

    public fun update(vararg values: NamedSqlParameter): JDBCResult<Int> = update(values.asIterable())

    public fun update(values: Iterable<NamedSqlParameter>): JDBCResult<Int>

    public interface ParametersScope {
        public fun <ValueT> set(name: String, value: ValueT, setter: SqlParameterSetter<ValueT>)
    }
}

public inline fun JdbcNamedPreparedStatement.setParameters(
    setter: ParametersScope.() -> Unit
): JDBCResult<JdbcNamedPreparedStatement> {
    val scope = NamedParametersScope(this)
    return withRaise(scope, wrap = { error -> error.asFailure() }) {
        setter(this)
        success(this@setParameters)
    }
}

@PublishedApi
internal class NamedParametersScope(
    private val statement: JdbcNamedPreparedStatement
) : ParametersScope,
    Raise<JDBCError> {

    override fun <ValueT> set(name: String, value: ValueT, setter: SqlParameterSetter<ValueT>) {
        statement.setParameter(name, value, setter)
            .onFailure { raise(it) }
    }

    override fun raise(error: JDBCError): Nothing {
        doRaise(error)
    }
}
