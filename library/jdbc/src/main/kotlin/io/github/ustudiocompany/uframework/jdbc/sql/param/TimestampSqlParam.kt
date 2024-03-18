package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement
import java.sql.Timestamp

public infix fun Timestamp?.asSqlParam(name: String): SqlParam = TimestampSqlParam(name, this)

private class TimestampSqlParam(override val name: String, private val value: Timestamp?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setTimestamp(position, value)
        else
            setNull(position, java.sql.Types.TIMESTAMP)
    }
}
