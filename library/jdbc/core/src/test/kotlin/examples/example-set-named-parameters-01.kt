// This file was automatically generated from JBDCNamedPreparedStatement.kt by Knit tool. Do not edit.
package examples.exampleSetNamedParameters01

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.intSqlParameterSetter
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.stringSqlParameterSetter
import io.github.ustudiocompany.uframework.jdbc.statement.JBDCNamedPreparedStatement
import io.github.ustudiocompany.uframework.jdbc.statement.setParameters

internal data class User(val id: Int, val name: String)

internal fun JBDCNamedPreparedStatement.initParams(user: User): JDBCResult<JBDCNamedPreparedStatement> =
    setParameters {
        set("id", user.id, intSqlParameterSetter)
        set("name", user.name, stringSqlParameterSetter)
    }
