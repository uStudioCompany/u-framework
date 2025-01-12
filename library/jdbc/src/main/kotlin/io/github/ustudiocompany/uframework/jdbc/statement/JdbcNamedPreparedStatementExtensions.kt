@file:Suppress("TooManyFunctions")

package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.apply
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRows
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.NamedSqlParameter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter

public fun JDBCResult<JdbcNamedPreparedStatement>.setParameter(
    param: NamedSqlParameter
): JDBCResult<JdbcNamedPreparedStatement> =
    this.apply { setParameter(param) }

public fun <T> JDBCResult<JdbcNamedPreparedStatement>.setParameter(
    name: String,
    value: T,
    setter: SqlParameterSetter<T>
): JDBCResult<JdbcNamedPreparedStatement> =
    this.apply { setParameter(name, value, setter) }

public fun JDBCResult<JdbcNamedPreparedStatement>.execute(
    vararg values: NamedSqlParameter
): JDBCResult<StatementResult> =
    this.execute(Iterable { values.iterator() })

public fun JDBCResult<JdbcNamedPreparedStatement>.execute(
    values: Iterable<NamedSqlParameter>
): JDBCResult<StatementResult> =
    andThen { it.execute(values) }

public fun JDBCResult<JdbcNamedPreparedStatement>.query(vararg values: NamedSqlParameter): JDBCResult<ResultRows> =
    this.query(Iterable { values.iterator() })

public fun JDBCResult<JdbcNamedPreparedStatement>.query(values: Iterable<NamedSqlParameter>): JDBCResult<ResultRows> =
    andThen { it.query(values) }

public fun JDBCResult<JdbcNamedPreparedStatement>.update(vararg values: NamedSqlParameter): JDBCResult<Int> =
    this.update(Iterable { values.iterator() })

public fun JDBCResult<JdbcNamedPreparedStatement>.update(values: Iterable<NamedSqlParameter>): JDBCResult<Int> =
    andThen { it.update(values) }
