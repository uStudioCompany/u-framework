package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement

public infix fun Long?.asSqlParam(name: String): SqlParam = sqlParam(name, this)
public fun sqlParam(name: String, value: Long?): SqlParam = LongSqlParam(name, value)

private class LongSqlParam(override val name: String, private val value: Long?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setLong(position, value)
        else
            setNull(position, java.sql.Types.BIGINT)
    }
}
