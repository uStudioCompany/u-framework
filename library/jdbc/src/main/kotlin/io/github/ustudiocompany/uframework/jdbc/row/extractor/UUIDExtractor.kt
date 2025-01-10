package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import java.sql.ResultSet
import java.util.*

internal fun ResultRow.getUUID(column: Int): JDBCResult<UUID?> =
    this.extractWith(column, UUID_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getObject(column, UUID::class.java)
        if (rs.wasNull()) Success.asNull else result.asSuccess()
    }

private val UUID_TYPE = Types("uuid")
