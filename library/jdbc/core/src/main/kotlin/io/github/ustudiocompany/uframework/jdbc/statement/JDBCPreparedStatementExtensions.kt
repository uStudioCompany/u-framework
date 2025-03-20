@file:Suppress("TooManyFunctions")

package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.apply
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter

public fun JDBCResult<JDBCPreparedStatement>.setParameter(
    index: Int,
    param: SqlParameter
): JDBCResult<JDBCPreparedStatement> =
    apply { setParameter(index, param) }

public fun <ValueT> JDBCResult<JDBCPreparedStatement>.setParameter(
    index: Int,
    value: ValueT,
    setter: SqlParameterSetter<ValueT>
): JDBCResult<JDBCPreparedStatement> =
    apply { setParameter(index, value, setter) }

public fun JDBCResult<JDBCPreparedStatement>.execute(vararg parameters: SqlParameter): JDBCResult<StatementResult> =
    this.execute(parameters.asIterable())

public fun JDBCResult<JDBCPreparedStatement>.execute(parameters: Iterable<SqlParameter>): JDBCResult<StatementResult> =
    andThen { it.execute(parameters) }

public fun JDBCResult<JDBCPreparedStatement>.query(vararg parameters: SqlParameter): JDBCResult<ResultRows> =
    this.query(parameters.asIterable())

public fun JDBCResult<JDBCPreparedStatement>.query(parameters: Iterable<SqlParameter>): JDBCResult<ResultRows> =
    andThen { it.query(parameters) }

public fun JDBCResult<JDBCPreparedStatement>.update(vararg parameters: SqlParameter): JDBCResult<Int> =
    this.update(parameters.asIterable())

public fun JDBCResult<JDBCPreparedStatement>.update(parameters: Iterable<SqlParameter>): JDBCResult<Int> =
    andThen { it.update(parameters) }
