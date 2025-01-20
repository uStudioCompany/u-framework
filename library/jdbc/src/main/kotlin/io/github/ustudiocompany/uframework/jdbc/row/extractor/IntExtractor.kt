package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import java.sql.ResultSet

public fun ResultRow.getInt(column: Int): JDBCResult<Int?> =
    this.extract(column, INT_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getInt(column)
        if (rs.wasNull()) null else result
    }

private val INT_TYPE = Types("int4")
