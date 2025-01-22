@file:Suppress("TooManyFunctions")

package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.apply
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter

public fun JDBCResult<JdbcPreparedStatement>.setParameter(
    index: Int,
    param: SqlParameter
): JDBCResult<JdbcPreparedStatement> = apply { setParameter(index, param) }

public fun <ValueT> JDBCResult<JdbcPreparedStatement>.setParameter(
    index: Int,
    value: ValueT,
    setter: SqlParameterSetter<ValueT>
): JDBCResult<JdbcPreparedStatement> =
    apply { setParameter(index, value, setter) }

public fun JDBCResult<JdbcPreparedStatement>.execute(vararg parameters: SqlParameter): JDBCResult<StatementResult> =
    this.execute(parameters.asIterable())

public fun JDBCResult<JdbcPreparedStatement>.execute(parameters: Iterable<SqlParameter>): JDBCResult<StatementResult> =
    andThen { it.execute(parameters) }

public fun JDBCResult<JdbcPreparedStatement>.query(vararg parameters: SqlParameter): JDBCResult<ResultRows> =
    this.query(parameters.asIterable())

public fun JDBCResult<JdbcPreparedStatement>.query(parameters: Iterable<SqlParameter>): JDBCResult<ResultRows> =
    andThen { it.query(parameters) }

public fun JDBCResult<JdbcPreparedStatement>.update(vararg parameters: SqlParameter): JDBCResult<Int> =
    this.update(parameters.asIterable())

public fun JDBCResult<JdbcPreparedStatement>.update(parameters: Iterable<SqlParameter>): JDBCResult<Int> =
    andThen { it.update(parameters) }
