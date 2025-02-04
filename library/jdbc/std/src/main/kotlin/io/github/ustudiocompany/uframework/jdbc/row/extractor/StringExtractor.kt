package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.ColumnTypes
import io.github.ustudiocompany.uframework.jdbc.row.ensureColumnValueNotNull
import java.sql.ResultSet

public fun ResultRow.getStringOrNull(column: Int): JDBCResult<String?> =
    this.extract(column, TEXT_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getString(column)
        if (rs.wasNull()) ResultK.Success.asNull else result.asSuccess()
    }

public fun ResultRow.getString(column: Int): JDBCResult<String> =
    ensureColumnValueNotNull(column, ResultRow::getStringOrNull)

private val TEXT_TYPE = ColumnTypes("text", "varchar", "bpchar")
