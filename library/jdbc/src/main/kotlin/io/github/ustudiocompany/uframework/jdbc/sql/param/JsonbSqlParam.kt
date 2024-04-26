package io.github.ustudiocompany.uframework.jdbc.sql.param

import org.postgresql.util.PGobject
import java.sql.PreparedStatement

public infix fun String?.jsonbAsSqlParam(name: String): SqlParam = jsonbSqlParam(name, this)
public fun jsonbSqlParam(name: String, value: String?): SqlParam = JsonbSqlParam(name, value)

private class JsonbSqlParam(override val name: String, private val value: String?) : SqlParam() {
    override fun PreparedStatement.setValue(position: Int) {
        if (value != null) {
            val jsonbObject = PGobject()
            jsonbObject.type = "jsonb"
            jsonbObject.value = value
            setObject(position, jsonbObject)
        } else
            setNull(position, java.sql.Types.OTHER)
    }
}
