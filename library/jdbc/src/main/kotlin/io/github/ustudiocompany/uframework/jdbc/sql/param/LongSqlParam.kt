package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement

public infix fun Long.asSqlParam(name: String): SqlParam = LongSqlParam(name, this)

private class LongSqlParam(override val name: String, private val value: Long) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        setLong(position, value)
    }
}
