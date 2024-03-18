package io.github.ustudiocompany.uframework.jdbc.sql.param

import java.sql.JDBCType
import java.sql.PreparedStatement
import java.util.*

public infix fun UUID?.asSqlParam(name: String): SqlParam = UUIDSqlParam(name, this)

private class UUIDSqlParam(override val name: String, private val value: UUID?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null)
            setObject(position, value, JDBCType.JAVA_OBJECT)
        else
            setNull(position, java.sql.Types.JAVA_OBJECT, UUID::class.qualifiedName)
    }
}
