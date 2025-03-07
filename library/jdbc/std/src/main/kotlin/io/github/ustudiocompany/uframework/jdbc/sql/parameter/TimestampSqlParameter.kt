package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Timestamp
import java.sql.Types

public fun Timestamp?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: Timestamp?): SqlParameter = TimestampSqlParameter(value)

public infix fun Timestamp?.asSqlParam(name: String): NamedSqlParameter = sqlParam(name, this)
public fun sqlParam(name: String, value: Timestamp?): NamedSqlParameter = TimestampNamedSqlParameter(name, value)

public val TimestampSqlParameterSetter: SqlParameterSetter<Timestamp> = { index, value ->
    if (value != null)
        setTimestamp(index, value)
    else
        setNull(index, Types.TIMESTAMP)
}

private class TimestampSqlParameter(private val value: Timestamp?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        TimestampSqlParameterSetter(statement, index, value)
}

private class TimestampNamedSqlParameter(
    override val name: String,
    private val value: Timestamp?
) : NamedSqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        TimestampSqlParameterSetter(statement, index, value)
}
