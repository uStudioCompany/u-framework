package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement

public infix fun Boolean?.asSqlParam(name: String): SqlParam = BooleanSqlParam(name, this)

private class BooleanSqlParam(override val name: String, private val value: Boolean?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setBoolean(position, value)
        else
            setNull(position, java.sql.Types.BOOLEAN)
    }
}
