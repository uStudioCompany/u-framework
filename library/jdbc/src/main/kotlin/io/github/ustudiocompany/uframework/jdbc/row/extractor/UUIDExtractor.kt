package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import java.sql.ResultSet
import java.util.*

public fun ResultRow.getUUID(column: Int): JDBCResult<UUID?> =
    this.extract(column, UUID_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getObject(column, UUID::class.java)
        if (rs.wasNull()) null else result
    }

private val UUID_TYPE = Types("uuid")
