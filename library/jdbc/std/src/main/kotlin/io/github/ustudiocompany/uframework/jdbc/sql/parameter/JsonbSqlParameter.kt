package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import org.postgresql.util.PGobject
import java.sql.PreparedStatement
import java.sql.Types

public fun String?.asJSONBSqlParam(): SqlParameter = jsonbSqlParam(this)
public fun jsonbSqlParam(value: String?): SqlParameter = JsonbSqlParameter(value)

public infix fun String?.asJSONBSqlParam(name: String): SqlParameter = jsonbSqlParam(name, this)
public fun jsonbSqlParam(name: String, value: String?): SqlParameter = JsonbNamedSqlParameter(name, value)

public val jsonbSqlParameterSetter: SqlParameterSetter<String> = { index, value ->
    if (value != null) {
        val jsonbObject = PGobject()
        jsonbObject.type = "jsonb"
        jsonbObject.value = value
        setObject(index, jsonbObject)
    } else
        setNull(index, Types.OTHER)
}

private class JsonbSqlParameter(private val value: String?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        jsonbSqlParameterSetter(statement, index, value)
}

private class JsonbNamedSqlParameter(
    override val name: String,
    private val value: String?
) : NamedSqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        jsonbSqlParameterSetter(statement, index, value)
}
