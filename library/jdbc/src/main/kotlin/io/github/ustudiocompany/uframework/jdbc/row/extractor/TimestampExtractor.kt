package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import java.sql.ResultSet
import java.sql.Timestamp

public fun ResultRow.getTimestamp(column: Int): JDBCResult<Timestamp?> =
    this.extract(column, TIMESTAMP_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getTimestamp(column)
        if (rs.wasNull()) null else result
    }

private val TIMESTAMP_TYPE = Types("timestamp")
