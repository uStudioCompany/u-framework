package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import io.github.ustudiocompany.uframework.jdbc.row.ensureColumnValueNotNull
import java.sql.ResultSet

public fun ResultRow.getBooleanOrNull(column: Int): JDBCResult<Boolean?> =
    this.extract(column, BOOL_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getBoolean(column)
        if (rs.wasNull()) ResultK.Success.asNull else result.asSuccess()
    }

public fun ResultRow.getBoolean(column: Int): JDBCResult<Boolean> =
    ensureColumnValueNotNull(column, ResultRow::getBooleanOrNull)

private val BOOL_TYPE = Types("bool")
