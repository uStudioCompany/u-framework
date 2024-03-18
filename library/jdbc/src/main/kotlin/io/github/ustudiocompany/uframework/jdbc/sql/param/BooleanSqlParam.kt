package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement

public infix fun Boolean?.asSqlParam(name: String): SqlParam = sqlParam(name, this)
public fun sqlParam(name: String, value: Boolean?): SqlParam = BooleanSqlParam(name, value)

private class BooleanSqlParam(override val name: String, private val value: Boolean?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setBoolean(position, value)
        else
            setNull(position, java.sql.Types.BOOLEAN)
    }
}
