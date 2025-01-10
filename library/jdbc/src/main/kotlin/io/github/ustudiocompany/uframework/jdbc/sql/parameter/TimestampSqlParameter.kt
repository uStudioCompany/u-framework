package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Timestamp
import java.sql.Types

public fun Timestamp?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: Timestamp?): SqlParameter = TimestampSqlParameter(value)

public class TimestampSqlParameter(private val value: Timestamp?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit = invoke(statement, index, value)

    public companion object : SqlParameterSetter<Timestamp> {

        @JvmStatic
        override operator fun invoke(statement: PreparedStatement, index: Int, value: Timestamp?) {
            if (value != null)
                statement.setTimestamp(index, value)
            else
                statement.setNull(index, Types.TIMESTAMP)
        }
    }
}
