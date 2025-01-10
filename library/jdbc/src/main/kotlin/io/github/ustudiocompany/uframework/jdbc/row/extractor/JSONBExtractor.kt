package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import org.postgresql.util.PGobject
import java.sql.ResultSet

internal fun ResultRow.getJSONB(column: Int): JDBCResult<String?> =
    this.extractWith(column, JSONB_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getObject(column, PGobject::class.java)
        if (rs.wasNull()) Success.asNull else result.value.asSuccess()
    }

private val JSONB_TYPE = Types("jsonb")
