package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import java.sql.ResultSet

public fun ResultRow.getString(column: Int): JDBCResult<String?> =
    this.extract(column, TEXT_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getString(column)
        if (rs.wasNull()) null else result
    }

private val TEXT_TYPE = Types("text", "varchar", "bpchar")
