package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import java.sql.PreparedStatement
import java.sql.Types

public fun String?.asSqlParam(): SqlParameter = sqlParam(this)
public fun sqlParam(value: String?): SqlParameter = StringSqlParameter(value)

public class StringSqlParameter(private val value: String?) : SqlParameter {
    override fun setValue(statement: PreparedStatement, index: Int): Unit = invoke(statement, index, value)

    public companion object : SqlParameterSetter<String> {

        @JvmStatic
        override operator fun invoke(statement: PreparedStatement, index: Int, value: String?) {
            if (value != null)
                statement.setString(index, value)
            else
                statement.setNull(index, Types.VARCHAR)
        }
    }
}
