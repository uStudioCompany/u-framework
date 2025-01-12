package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import java.sql.ResultSet

public fun ResultRow.getInt(column: Int): JDBCResult<Int?> =
    this.extractWith(column, INT_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getInt(column)
        if (rs.wasNull()) Success.asNull else result.asSuccess()
    }

private val INT_TYPE = Types("int4")
