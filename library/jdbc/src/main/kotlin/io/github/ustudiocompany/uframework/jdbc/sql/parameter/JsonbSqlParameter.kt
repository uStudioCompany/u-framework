package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import org.postgresql.util.PGobject
import java.sql.PreparedStatement
import java.sql.Types

public fun String?.asJSONBSqlParam(): SqlParameter = jsonbSqlParam(this)
public fun jsonbSqlParam(value: String?): SqlParameter = JsonbSqlParameter(value)

public class JsonbSqlParameter(private val value: String?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit = invoke(statement, index, value)

    public companion object : SqlParameterSetter<String> {

        @JvmStatic
        override operator fun invoke(statement: PreparedStatement, index: Int, value: String?) {
            if (value != null) {
                val jsonbObject = PGobject()
                jsonbObject.type = "jsonb"
                jsonbObject.value = value
                statement.setObject(index, jsonbObject)
            } else
                statement.setNull(index, Types.OTHER)
        }
    }
}
