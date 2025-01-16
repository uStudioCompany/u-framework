package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.Raise
import io.github.airflux.commons.types.doRaise
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.onFailure
import io.github.airflux.commons.types.resultk.success
import io.github.airflux.commons.types.withRaise
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
import io.github.ustudiocompany.uframework.jdbc.statement.JdbcPreparedStatement.ParametersScope

public interface JdbcPreparedStatement : JdbcStatement {

    public fun clearParameters()

    public fun setParameter(index: Int, param: SqlParameter): JDBCResult<Unit>

    public fun <T> setParameter(
        index: Int,
        value: T,
        setter: SqlParameterSetter<T>
    ): JDBCResult<Unit>

    public fun execute(vararg values: SqlParameter): JDBCResult<StatementResult> = execute(values.asIterable())

    public fun execute(values: Iterable<SqlParameter>): JDBCResult<StatementResult>

    public fun query(vararg values: SqlParameter): JDBCResult<ResultRows> = query(values.asIterable())

    public fun query(values: Iterable<SqlParameter>): JDBCResult<ResultRows>

    public fun update(vararg values: SqlParameter): JDBCResult<Int> = update(values.asIterable())

    public fun update(values: Iterable<SqlParameter>): JDBCResult<Int>

    public interface ParametersScope {
        public fun <T> set(index: Int, value: T, setter: SqlParameterSetter<T>)
    }
}

public inline fun JdbcPreparedStatement.setParameters(
    setter: ParametersScope.() -> Unit
): JDBCResult<JdbcPreparedStatement> {
    val scope = IndexesParametersScope(this)
    return withRaise(scope, wrap = { error -> error.asFailure() }) {
        setter(this)
        success(this@setParameters)
    }
}

@PublishedApi
internal class IndexesParametersScope(
    private val statement: JdbcPreparedStatement
) : ParametersScope,
    Raise<JDBCError> {

    override fun <T> set(index: Int, value: T, setter: SqlParameterSetter<T>) {
        statement.setParameter(index, value, setter)
            .onFailure { raise(it) }
    }

    override fun raise(error: JDBCError): Nothing {
        doRaise(error)
    }
}
