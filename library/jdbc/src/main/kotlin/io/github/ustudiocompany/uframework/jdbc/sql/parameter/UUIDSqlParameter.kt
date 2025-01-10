package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Types
import java.util.*

public fun UUID?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: UUID?): SqlParameter = UUIDSqlParameter(value)

public class UUIDSqlParameter(private val value: UUID?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit = invoke(statement, index, value)

    public companion object : SqlParameterSetter<UUID> {

        @JvmStatic
        override operator fun invoke(statement: PreparedStatement, index: Int, value: UUID?) {
            if (value != null)
                statement.setObject(index, value)
            else
                statement.setNull(index, Types.JAVA_OBJECT, UUID::class.qualifiedName)
        }
    }
}
