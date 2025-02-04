package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Types

public fun Boolean?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: Boolean?): SqlParameter = BooleanSqlParameter(value)

public infix fun Boolean?.asSqlParam(name: String): NamedSqlParameter = sqlParam(name, this)
public fun sqlParam(name: String, value: Boolean?): NamedSqlParameter = BooleanNamedSqlParameter(name, value)

public val BooleanSqlParameterSetter: SqlParameterSetter<Boolean> = { index, value ->
    if (value != null)
        setBoolean(index, value)
    else
        setNull(index, Types.BOOLEAN)
}

private class BooleanSqlParameter(private val value: Boolean?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        BooleanSqlParameterSetter(statement, index, value)
}

private class BooleanNamedSqlParameter(
    override val name: String,
    private val value: Boolean?
) : NamedSqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        BooleanSqlParameterSetter(statement, index, value)
}
