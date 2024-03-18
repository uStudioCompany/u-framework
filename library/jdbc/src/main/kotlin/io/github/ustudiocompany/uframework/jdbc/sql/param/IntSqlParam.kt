package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement
import java.sql.Types

public infix fun Int?.asSqlParam(name: String): SqlParam = sqlParam(name, this)
public fun sqlParam(name: String, value: Int?): SqlParam = IntSqlParam(name, value)

private class IntSqlParam(override val name: String, private val value: Int?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setInt(position, value)
        else
            setNull(position, Types.INTEGER)
    }
}
