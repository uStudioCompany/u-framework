package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.Types
import io.github.ustudiocompany.uframework.jdbc.row.ensureColumnValueNotNull
import java.sql.ResultSet

public fun ResultRow.getIntOrNull(column: Int): JDBCResult<Int?> =
    this.extract(column, INT_TYPE) { column: Int, rs: ResultSet ->
        val result = rs.getInt(column)
        if (rs.wasNull()) ResultK.Success.asNull else result.asSuccess()
    }

public fun ResultRow.getInt(column: Int): JDBCResult<Int> =
    ensureColumnValueNotNull(column, ResultRow::getIntOrNull)

private val INT_TYPE = Types("int4")
