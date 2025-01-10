package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Types

public fun Int?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: Int?): SqlParameter = IntSqlParameter(value)

public class IntSqlParameter(private val value: Int?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit = invoke(statement, index, value)

    public companion object : SqlParameterSetter<Int> {

        @JvmStatic
        override operator fun invoke(statement: PreparedStatement, index: Int, value: Int?) {
            if (value != null)
                statement.setInt(index, value)
            else
                statement.setNull(index, Types.INTEGER)
        }
    }
}
