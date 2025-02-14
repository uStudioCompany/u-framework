package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.Raise
import io.github.airflux.commons.types.doRaise
import io.github.airflux.commons.types.maybe.Maybe
import io.github.airflux.commons.types.maybe.onSome
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.success
import io.github.airflux.commons.types.withRaise
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCPreparedStatement.ParametersScope

/**
 * The type representing a JDBC prepared statement.
 */
public interface JBDCPreparedStatement : JBDCStatement {

    /**
     * Clears all the parameters.
     */
    public fun clearParameters()

    /**
     * Sets the parameter.
     *
     * @param index the index of the parameter.
     * @param param the parameter.
     */
    public fun setParameter(index: Int, param: SqlParameter): Maybe<JDBCError>

    /**
     * Sets the parameter.
     *
     * @param index the index of the parameter.
     * @param value the value of the parameter.
     * @param setter the setter for the parameter.
     */
    public fun <ValueT> setParameter(
        index: Int,
        value: ValueT,
        setter: SqlParameterSetter<ValueT>
    ): Maybe<JDBCError>

    /**
     * Executes the prepared statement.
     *
     * @param values the parameters.
     * @return the result of the execution.
     */
    public fun execute(vararg values: SqlParameter): JDBCResult<StatementResult> = execute(values.asIterable())

    /**
     * Executes the prepared statement.
     *
     * @param values the parameters.
     * @return the result of the execution.
     */
    public fun execute(values: Iterable<SqlParameter>): JDBCResult<StatementResult>

    /**
     * Executes the query.
     *
     * @param values the parameters.
     * @return the rows with the results or an error of the execution.
     */
    public fun query(vararg values: SqlParameter): JDBCResult<ResultRows> = query(values.asIterable())

    /**
     * Executes the query.
     *
     * @param values the parameters.
     * @return the rows with the results or an error of the execution.
     */
    public fun query(values: Iterable<SqlParameter>): JDBCResult<ResultRows>

    /**
     * Updates the data.
     *
     * @param values the parameters.
     * @return the number of updated rows or an error of the execution.
     */
    public fun update(vararg values: SqlParameter): JDBCResult<Int> = update(values.asIterable())

    /**
     * Updates the data.
     *
     * @param values the parameters.
     * @return the number of updated rows or an error of the execution.
     */
    public fun update(values: Iterable<SqlParameter>): JDBCResult<Int>

    public interface ParametersScope {
        public fun <ValueT> set(index: Int, value: ValueT, setter: SqlParameterSetter<ValueT>)
    }
}

/**
 * Sets the parameters.
 *
 * @param setter the block of a code that sets the parameters.
 * @return an instance of [JBDCPreparedStatement] to which parameters were set or an instance of [JDBCError]
 * if the configuration fails.
 */
public inline fun JBDCPreparedStatement.setParameters(
    setter: ParametersScope.() -> Unit
): JDBCResult<JBDCPreparedStatement> {
    val scope = IndexesParametersScope(this)
    return withRaise(scope, wrap = { error -> error.asFailure() }) {
        setter(this)
        success(this@setParameters)
    }
}

@PublishedApi
internal class IndexesParametersScope(
    private val statement: JBDCPreparedStatement
) : ParametersScope,
    Raise<JDBCError> {

    override fun <ValueT> set(index: Int, value: ValueT, setter: SqlParameterSetter<ValueT>) {
        statement.setParameter(index, value, setter)
            .onSome { raise(it) }
    }

    override fun raise(error: JDBCError): Nothing {
        doRaise(error)
    }
}
