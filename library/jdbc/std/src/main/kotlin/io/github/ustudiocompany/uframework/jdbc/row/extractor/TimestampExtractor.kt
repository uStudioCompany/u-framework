package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.ColumnTypes
import io.github.ustudiocompany.uframework.jdbc.row.ensureColumnValueNotNull
import java.sql.ResultSet
import java.sql.Timestamp

public fun ResultRow.getTimestampOrNull(column: Int): JDBCResult<Timestamp?> =
    this.extract(column, TIMESTAMP_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getTimestamp(column)
        if (rs.wasNull()) ResultK.Success.asNull else result.asSuccess()
    }

public fun ResultRow.getTimestamp(column: Int): JDBCResult<Timestamp> =
    ensureColumnValueNotNull(column, ResultRow::getTimestampOrNull)

private val TIMESTAMP_TYPE = ColumnTypes("timestamp")
