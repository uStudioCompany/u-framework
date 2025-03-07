// This file was automatically generated from JDBCNamedPreparedStatement.kt by Knit tool. Do not edit.
package examples.exampleSetNamedParameters01

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.IntSqlParameterSetter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.StringSqlParameterSetter
import io.github.ustudiocompany.uframework.jdbc.statement.JDBCNamedPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.setParameters

internal data class User(val id: Int, val name: String)

internal fun JDBCNamedPreparedStatement.initParams(user: User): JDBCResult<JDBCNamedPreparedStatement> =
    setParameters {
        set("id", user.id, IntSqlParameterSetter)
        set("name", user.name, StringSqlParameterSetter)
    }
