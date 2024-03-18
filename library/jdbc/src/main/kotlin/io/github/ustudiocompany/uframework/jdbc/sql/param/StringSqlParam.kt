package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement

public infix fun String?.asSqlParam(name: String): SqlParam = StringSqlParam(name, this)

private class StringSqlParam(override val name: String, private val value: String?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setString(position, value)
        else
            setNull(position, java.sql.Types.VARCHAR)
    }
}
