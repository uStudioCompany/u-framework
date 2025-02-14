package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Types

public fun Long?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: Long?): SqlParameter = LongSqlParameter(value)

public infix fun Long?.asSqlParam(name: String): NamedSqlParameter = sqlParam(name, this)
public fun sqlParam(name: String, value: Long?): NamedSqlParameter = LongNamedSqlParameter(name, value)

public val LongSqlParameterSetter: SqlParameterSetter<Long> = { index, value ->
    if (value != null)
        setLong(index, value)
    else
        setNull(index, Types.INTEGER)
}

private class LongSqlParameter(private val value: Long?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        LongSqlParameterSetter(statement, index, value)
}

private class LongNamedSqlParameter(
    override val name: String,
    private val value: Long?
) : NamedSqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        LongSqlParameterSetter(statement, index, value)
}
