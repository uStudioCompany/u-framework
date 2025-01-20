package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import org.postgresql.util.PGobject
import java.sql.ResultSet

public fun ResultRow.getJSONB(column: Int): JDBCResult<String?> =
    this.extract(column, JSONB_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getObject(column, PGobject::class.java)
        if (rs.wasNull()) null else result.value
    }

private val JSONB_TYPE = Types("jsonb")
