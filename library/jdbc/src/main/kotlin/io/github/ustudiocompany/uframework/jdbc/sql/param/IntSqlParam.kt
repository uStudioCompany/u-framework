package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement

public infix fun Int?.asSqlParam(name: String): SqlParam = IntSqlParam(name, this)

private class IntSqlParam(override val name: String, private val value: Int?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setInt(position, value)
        else
            setNull(position, java.sql.Types.INTEGER)
    }
}
