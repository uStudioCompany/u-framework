package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public infix fun String?.asSqlParam(name: String): SqlParam = sqlParam(name, this)

@Deprecated(message = "Do not use.", level = DeprecationLevel.WARNING)
public fun sqlParam(name: String, value: String?): SqlParam = StringSqlParam(name, value)

private class StringSqlParam(override val name: String, private val value: String?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setString(position, value)
        else
            setNull(position, java.sql.Types.VARCHAR)
    }
}
