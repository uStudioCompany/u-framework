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
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.NamedSqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCNamedPreparedStatement.ParametersScope

/**
 * The type representing a named JDBC prepared statement.
 */
public interface JBDCNamedPreparedStatement : JBDCStatement {

    /**
     * Clears all the parameters.
     */
    public fun clearParameters()

    /**
     * Sets the parameter.
     *
     * @param param the named parameter.
     */
    public fun setParameter(param: NamedSqlParameter): Maybe<JDBCError>

    /**
     * Sets the parameter.
     *
     * @param name the name of the parameter.
     * @param value the value of the parameter.
     * @param setter the setter for the parameter.
     */
    public fun <ValueT> setParameter(
        name: String,
        value: ValueT,
        setter: SqlParameterSetter<ValueT>
    ): Maybe<JDBCError>

    /**
     * Executes the named prepared statement.
     *
     * @param values the named parameters.
     * @return the result of the execution.
     */
    public fun execute(vararg values: NamedSqlParameter): JDBCResult<StatementResult> = execute(values.asIterable())

    /**
     * Executes the named prepared statement.
     *
     * @param values the named parameters.
     * @return the result of the execution.
     */
    public fun execute(values: Iterable<NamedSqlParameter>): JDBCResult<StatementResult>

    /**
     * Executes the query.
     *
     * @param values the named parameters.
     * @return the rows with the results or an error of the execution.
     */
    public fun query(vararg values: NamedSqlParameter): JDBCResult<ResultRows> = query(values.asIterable())

    /**
     * Executes the query.
     *
     * @param values the named parameters.
     * @return the rows with the results or an error of the execution.
     */
    public fun query(values: Iterable<NamedSqlParameter>): JDBCResult<ResultRows>

    /**
     * Updates the data.
     *
     * @param values the named parameters.
     * @return the number of updated rows or an error of the execution.
     */
    public fun update(vararg values: NamedSqlParameter): JDBCResult<Int> = update(values.asIterable())

    /**
     * Updates the data.
     *
     * @param values the named parameters.
     * @return the number of updated rows or an error of the execution.
     */
    public fun update(values: Iterable<NamedSqlParameter>): JDBCResult<Int>

    public interface ParametersScope {
        public fun <ValueT> set(name: String, value: ValueT, setter: SqlParameterSetter<ValueT>)
    }
}

/**
 * Sets the parameters.
 *
 * Example:
 * <!--- INCLUDE
 * import io.github.ustudiocompany.uframework.jdbc.JDBCResult
 * import io.github.ustudiocompany.uframework.jdbc.sql.parameter.IntSqlParameterSetter
 * import io.github.ustudiocompany.uframework.jdbc.sql.parameter.StringSqlParameterSetter
 * import io.github.ustudiocompany.uframework.jdbc.statement.JBDCNamedPreparedStatement
 * import io.github.ustudiocompany.uframework.jdbc.statement.setParameters
 * -->
 * ```kotlin
 * internal data class User(val id: Int, val name: String)
 *
 * internal fun JBDCNamedPreparedStatement.initParams(user: User): JDBCResult<JBDCNamedPreparedStatement> =
 *     setParameters {
 *         set("id", user.id, IntSqlParameterSetter)
 *         set("name", user.name, StringSqlParameterSetter)
 *     }
 * ```
 * <!--- KNIT example-set-named-parameters-01.kt -->
 *
 * @param setter the block of a code that sets the parameters.
 * @return an instance of [JBDCNamedPreparedStatement] to which parameters were set or an instance of [JDBCError]
 * if the configuration fails.
 */
public inline fun JBDCNamedPreparedStatement.setParameters(
    setter: ParametersScope.() -> Unit
): JDBCResult<JBDCNamedPreparedStatement> {
    val scope = NamedParametersScope(this)
    return withRaise(scope, wrap = { error -> error.asFailure() }) {
        setter(this)
        success(this@setParameters)
    }
}

@PublishedApi
internal class NamedParametersScope(
    private val statement: JBDCNamedPreparedStatement
) : ParametersScope,
    Raise<JDBCError> {

    override fun <ValueT> set(name: String, value: ValueT, setter: SqlParameterSetter<ValueT>) {
        statement.setParameter(name, value, setter)
            .onSome { raise(it) }
    }

    override fun raise(error: JDBCError): Nothing {
        doRaise(error)
    }
}
