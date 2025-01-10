package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Types

public fun Boolean?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: Boolean?): SqlParameter = BooleanSqlParameter(value)

public class BooleanSqlParameter(private val value: Boolean?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit = invoke(statement, index, value)

    public companion object : SqlParameterSetter<Boolean> {

        @JvmStatic
        override operator fun invoke(statement: PreparedStatement, index: Int, value: Boolean?) {
            if (value != null)
                statement.setBoolean(index, value)
            else
                statement.setNull(index, Types.BOOLEAN)
        }
    }
}
