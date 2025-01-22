package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import io.github.ustudiocompany.uframework.jdbc.row.ensureColumnValueNotNull
import java.sql.ResultSet
import java.util.*

public fun ResultRow.getUUIDOrNull(column: Int): JDBCResult<UUID?> =
    this.extract(column, UUID_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getObject(column, UUID::class.java)
        if (rs.wasNull()) ResultK.Success.asNull else result.asSuccess()
    }

public fun ResultRow.getUUID(column: Int): JDBCResult<UUID> =
    ensureColumnValueNotNull(column, ResultRow::getUUIDOrNull)

private val UUID_TYPE = Types("uuid")
