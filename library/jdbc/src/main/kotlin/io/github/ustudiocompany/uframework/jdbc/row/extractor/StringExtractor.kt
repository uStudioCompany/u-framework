package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import java.sql.ResultSet

internal fun ResultRow.getString(column: Int): JDBCResult<String?> =
    this.extractWith(column, TEXT_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getString(column)
        if (rs.wasNull()) Success.asNull else result.asSuccess()
    }

private val TEXT_TYPE = Types("text", "varchar", "bpchar")
