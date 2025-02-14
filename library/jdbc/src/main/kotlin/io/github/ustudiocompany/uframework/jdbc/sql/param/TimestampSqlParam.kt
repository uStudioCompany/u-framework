package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement
import java.sql.Timestamp

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public infix fun Timestamp?.asSqlParam(name: String): SqlParam = sqlParam(name, this)

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public fun sqlParam(name: String, value: Timestamp?): SqlParam = TimestampSqlParam(name, value)

private class TimestampSqlParam(override val name: String, private val value: Timestamp?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setTimestamp(position, value)
        else
            setNull(position, java.sql.Types.TIMESTAMP)
    }
}
