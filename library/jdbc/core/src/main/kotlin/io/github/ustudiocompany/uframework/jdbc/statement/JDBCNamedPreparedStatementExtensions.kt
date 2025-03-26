@file:Suppress("TooManyFunctions")

package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.apply
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.NamedSqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter

public fun JDBCResult<JDBCNamedPreparedStatement>.setParameter(
    param: NamedSqlParameter
): JDBCResult<JDBCNamedPreparedStatement> =
    apply { setParameter(param) }

public fun <ValueT> JDBCResult<JDBCNamedPreparedStatement>.setParameter(
    name: String,
    value: ValueT,
    setter: SqlParameterSetter<ValueT>
): JDBCResult<JDBCNamedPreparedStatement> =
    apply { setParameter(name, value, setter) }

public fun JDBCResult<JDBCNamedPreparedStatement>.execute(
    vararg values: NamedSqlParameter
): JDBCResult<StatementResult> =
    this.execute(values.asIterable())

public fun JDBCResult<JDBCNamedPreparedStatement>.execute(
    values: Iterable<NamedSqlParameter>
): JDBCResult<StatementResult> =
    andThen { it.execute(values) }

public fun JDBCResult<JDBCNamedPreparedStatement>.query(vararg values: NamedSqlParameter): JDBCResult<ResultRows> =
    this.query(values.asIterable())

public fun JDBCResult<JDBCNamedPreparedStatement>.query(values: Iterable<NamedSqlParameter>): JDBCResult<ResultRows> =
    andThen { it.query(values) }

public fun JDBCResult<JDBCNamedPreparedStatement>.update(vararg values: NamedSqlParameter): JDBCResult<Int> =
    this.update(values.asIterable())

public fun JDBCResult<JDBCNamedPreparedStatement>.update(values: Iterable<NamedSqlParameter>): JDBCResult<Int> =
    andThen { it.update(values) }
