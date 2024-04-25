package io.github.ustudiocompany.uframework.jdbc.sql.param

import org.postgresql.util.PGobject
import java.sql.PreparedStatement

public infix fun String?.jsonAsSqlParam(name: String): SqlParam = jsonSqlParam(name, this)
public fun jsonSqlParam(name: String, value: String?): SqlParam = JsonSqlParam(name, value)

private class JsonSqlParam(override val name: String, private val value: String?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null) {
            val jsonbObject = PGobject()
            jsonbObject.type = "json"
            jsonbObject.value = value
            setObject(position, jsonbObject)
        } else
            setNull(position, java.sql.Types.OTHER)
    }
}
