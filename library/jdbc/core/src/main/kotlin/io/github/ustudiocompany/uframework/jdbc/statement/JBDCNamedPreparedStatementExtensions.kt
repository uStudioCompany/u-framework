@file:Suppress("TooManyFunctions")

package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.maybe.fold
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.apply
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.NamedSqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter

public fun JDBCResult<JBDCNamedPreparedStatement>.setParameter(
    param: NamedSqlParameter
): JDBCResult<JBDCNamedPreparedStatement> =
    apply {
        setParameter(param)
            .fold(
                onSome = { it.asFailure() },
                onNone = { ResultK.Success.asUnit }
            )
    }

public fun <ValueT> JDBCResult<JBDCNamedPreparedStatement>.setParameter(
    name: String,
    value: ValueT,
    setter: SqlParameterSetter<ValueT>
): JDBCResult<JBDCNamedPreparedStatement> =
    apply {
        setParameter(name, value, setter)
            .fold(
                onSome = { it.asFailure() },
                onNone = { ResultK.Success.asUnit }
            )
    }

public fun JDBCResult<JBDCNamedPreparedStatement>.execute(
    vararg values: NamedSqlParameter
): JDBCResult<StatementResult> =
    this.execute(values.asIterable())

public fun JDBCResult<JBDCNamedPreparedStatement>.execute(
    values: Iterable<NamedSqlParameter>
): JDBCResult<StatementResult> =
    andThen { it.execute(values) }

public fun JDBCResult<JBDCNamedPreparedStatement>.query(vararg values: NamedSqlParameter): JDBCResult<ResultRows> =
    this.query(values.asIterable())

public fun JDBCResult<JBDCNamedPreparedStatement>.query(values: Iterable<NamedSqlParameter>): JDBCResult<ResultRows> =
    andThen { it.query(values) }

public fun JDBCResult<JBDCNamedPreparedStatement>.update(vararg values: NamedSqlParameter): JDBCResult<Int> =
    this.update(values.asIterable())

public fun JDBCResult<JBDCNamedPreparedStatement>.update(values: Iterable<NamedSqlParameter>): JDBCResult<Int> =
    andThen { it.update(values) }
