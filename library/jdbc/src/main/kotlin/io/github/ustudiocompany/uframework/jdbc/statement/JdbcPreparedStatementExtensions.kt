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
): JDBCResult<JdbcPreparedStatement> =
    this.apply { setParameter(index, param) }

public fun <T> JDBCResult<JdbcPreparedStatement>.setParameter(
    index: Int,
    value: T,
    setter: SqlParameterSetter<T>
): JDBCResult<JdbcPreparedStatement> =
    this.apply { setParameter(index, value, setter) }

public fun JDBCResult<JdbcPreparedStatement>.execute(vararg values: SqlParameter): JDBCResult<StatementResult> =
    this.execute(Iterable { values.iterator() })

public fun JDBCResult<JdbcPreparedStatement>.execute(values: Iterable<SqlParameter>): JDBCResult<StatementResult> =
    andThen { it.execute(values) }

public fun JDBCResult<JdbcPreparedStatement>.query(vararg values: SqlParameter): JDBCResult<ResultRows> =
    this.query(Iterable { values.iterator() })

public fun JDBCResult<JdbcPreparedStatement>.query(values: Iterable<SqlParameter>): JDBCResult<ResultRows> =
    andThen { it.query(values) }

public fun JDBCResult<JdbcPreparedStatement>.update(vararg values: SqlParameter): JDBCResult<Int> =
    this.update(Iterable { values.iterator() })

public fun JDBCResult<JdbcPreparedStatement>.update(values: Iterable<SqlParameter>): JDBCResult<Int> =
    andThen { it.update(values) }
