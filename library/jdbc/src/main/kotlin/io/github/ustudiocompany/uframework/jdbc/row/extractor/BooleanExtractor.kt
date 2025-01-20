package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import java.sql.ResultSet

public fun ResultRow.getBoolean(column: Int): JDBCResult<Boolean?> =
    this.extract(column, BOOL_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getBoolean(column)
        if (rs.wasNull()) null else result
    }

private val BOOL_TYPE = Types("bool")
