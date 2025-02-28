package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Types

public fun Int?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: Int?): SqlParameter = IntSqlParameter(value)

public infix fun Int?.asSqlParam(name: String): NamedSqlParameter = sqlParam(name, this)
public fun sqlParam(name: String, value: Int?): NamedSqlParameter = IntNamedSqlParameter(name, value)

public val IntSqlParameterSetter: SqlParameterSetter<Int> = { index, value ->
    if (value != null)
        setInt(index, value)
    else
        setNull(index, Types.INTEGER)
}

private class IntSqlParameter(private val value: Int?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        IntSqlParameterSetter(statement, index, value)
}

private class IntNamedSqlParameter(
    override val name: String,
    private val value: Int?
) : NamedSqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        IntSqlParameterSetter(statement, index, value)
}
