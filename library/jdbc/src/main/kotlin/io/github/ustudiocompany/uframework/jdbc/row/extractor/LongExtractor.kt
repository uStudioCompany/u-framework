package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import java.sql.ResultSet

public fun ResultRow.getLong(column: Int): JDBCResult<Long?> =
    this.extract(column, LONG_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getLong(column)
        if (rs.wasNull()) null else result
    }

private val LONG_TYPE = Types("int8")
