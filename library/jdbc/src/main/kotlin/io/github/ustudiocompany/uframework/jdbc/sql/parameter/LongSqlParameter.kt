package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Types

public fun Long?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: Long?): SqlParameter = LongSqlParameter(value)

public class LongSqlParameter(private val value: Long?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit = invoke(statement, index, value)

    public companion object : SqlParameterSetter<Long> {

        @JvmStatic
        override operator fun invoke(statement: PreparedStatement, index: Int, value: Long?) {
            if (value != null)
                statement.setLong(index, value)
            else
                statement.setNull(index, Types.BIGINT)
        }
    }
}
