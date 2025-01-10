package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import java.sql.ResultSet
import java.sql.Timestamp

internal fun ResultRow.getTimestamp(column: Int): JDBCResult<Timestamp?> =
    this.extractWith(column, TIMESTAMP_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getTimestamp(column)
        if (rs.wasNull()) Success.asNull else result.asSuccess()
    }

private val TIMESTAMP_TYPE = Types("timestamp")
