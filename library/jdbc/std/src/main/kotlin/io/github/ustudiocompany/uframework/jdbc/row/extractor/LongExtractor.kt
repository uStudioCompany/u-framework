package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.ColumnTypes
import io.github.ustudiocompany.uframework.jdbc.row.ensureColumnValueNotNull
import java.sql.ResultSet

public fun ResultRow.getLongOrNull(column: Int): JDBCResult<Long?> =
    this.extract(column, LONG_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getLong(column)
        if (rs.wasNull()) ResultK.Success.asNull else result.asSuccess()
    }

public fun ResultRow.getLong(column: Int): JDBCResult<Long> =
    ensureColumnValueNotNull(column, ResultRow::getLongOrNull)

private val LONG_TYPE = ColumnTypes("int8")
