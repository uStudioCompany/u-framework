package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Types

public fun String?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: String?): SqlParameter = StringSqlParameter(value)

public infix fun String?.asSqlParam(name: String): NamedSqlParameter = sqlParam(name, this)
public fun sqlParam(name: String, value: String?): NamedSqlParameter = StringNamedSqlParameter(name, value)

public val stringSqlParameterSetter: SqlParameterSetter<String> = { index, value ->
    if (value != null)
        setString(index, value)
    else
        setNull(index, Types.VARCHAR)
}

private class StringSqlParameter(private val value: String?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        stringSqlParameterSetter(statement, index, value)
}

private class StringNamedSqlParameter(
    override val name: String,
    private val value: String?
) : NamedSqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        stringSqlParameterSetter(statement, index, value)
}
