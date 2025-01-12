package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Types
import java.util.*

public fun UUID?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: UUID?): SqlParameter = UUIDSqlParameter(value)

public infix fun UUID?.asSqlParam(name: String): NamedSqlParameter = sqlParam(name, this)
public fun sqlParam(name: String, value: UUID?): NamedSqlParameter = UUIDNamedSqlParameter(name, value)

public val uuidSqlParameterSetter: SqlParameterSetter<UUID> = { index, value ->
    if (value != null)
        setObject(index, value)
    else
        setNull(index, Types.JAVA_OBJECT, UUID::class.qualifiedName)
}

public class UUIDSqlParameter(private val value: UUID?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        uuidSqlParameterSetter(statement, index, value)
}

private class UUIDNamedSqlParameter(
    override val name: String,
    private val value: UUID?
) : NamedSqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit =
        uuidSqlParameterSetter(statement, index, value)
}
