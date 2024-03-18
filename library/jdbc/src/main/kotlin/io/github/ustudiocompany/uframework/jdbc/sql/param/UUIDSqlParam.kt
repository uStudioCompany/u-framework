package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.PreparedStatement
import java.util.*

public infix fun UUID?.asSqlParam(name: String): SqlParam = sqlParam(name, this)
public fun sqlParam(name: String, value: UUID?): SqlParam = UUIDSqlParam(name, value)

private class UUIDSqlParam(override val name: String, private val value: UUID?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setObject(position, value)
        else
            setNull(position, java.sql.Types.JAVA_OBJECT, UUID::class.qualifiedName)
    }
}
