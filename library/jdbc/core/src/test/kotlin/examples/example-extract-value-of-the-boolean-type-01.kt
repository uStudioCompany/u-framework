// This file was automatically generated from ResultRow.kt by Knit tool. Do not edit.
package examples.exampleExtractValueOfTheBooleanType01

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.ColumnTypes
import java.sql.ResultSet

public fun ResultRow.getBoolean(column: Int): JDBCResult<Boolean?> =
    this.extract(column, ColumnTypes("bool")) { column: Int, rs: ResultSet ->
        val result = rs.getBoolean(column)
        if (rs.wasNull()) ResultK.Success.asNull else result.asSuccess()
    }
