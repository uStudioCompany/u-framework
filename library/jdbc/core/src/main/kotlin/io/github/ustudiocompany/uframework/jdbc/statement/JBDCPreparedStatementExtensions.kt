@file:Suppress("TooManyFunctions")

package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.maybe.fold
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.apply
import io.github.airflux.commons.types.resultk.asFailure
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter

public fun JDBCResult<JBDCPreparedStatement>.setParameter(
    index: Int,
    param: SqlParameter
): JDBCResult<JBDCPreparedStatement> = apply {
    setParameter(index, param)
        .fold(
            onSome = { it.asFailure() },
            onNone = { ResultK.Success.asUnit }
        )
}

public fun <ValueT> JDBCResult<JBDCPreparedStatement>.setParameter(
    index: Int,
    value: ValueT,
    setter: SqlParameterSetter<ValueT>
): JDBCResult<JBDCPreparedStatement> =
    apply {
        setParameter(index, value, setter)
            .fold(
                onSome = { it.asFailure() },
                onNone = { ResultK.Success.asUnit }
            )
    }

public fun JDBCResult<JBDCPreparedStatement>.execute(vararg parameters: SqlParameter): JDBCResult<StatementResult> =
    this.execute(parameters.asIterable())

public fun JDBCResult<JBDCPreparedStatement>.execute(parameters: Iterable<SqlParameter>): JDBCResult<StatementResult> =
    andThen { it.execute(parameters) }

public fun JDBCResult<JBDCPreparedStatement>.query(vararg parameters: SqlParameter): JDBCResult<ResultRows> =
    this.query(parameters.asIterable())

public fun JDBCResult<JBDCPreparedStatement>.query(parameters: Iterable<SqlParameter>): JDBCResult<ResultRows> =
    andThen { it.query(parameters) }

public fun JDBCResult<JBDCPreparedStatement>.update(vararg parameters: SqlParameter): JDBCResult<Int> =
    this.update(parameters.asIterable())

public fun JDBCResult<JBDCPreparedStatement>.update(parameters: Iterable<SqlParameter>): JDBCResult<Int> =
    andThen { it.update(parameters) }
