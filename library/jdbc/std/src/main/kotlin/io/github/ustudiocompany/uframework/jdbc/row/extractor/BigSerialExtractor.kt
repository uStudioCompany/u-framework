package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.asSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow.ColumnTypes
import java.sql.ResultSet

public fun ResultRow.getBigSerial(column: Int): JDBCResult<Long> =
    this.extract(column, BIG_SERIAL_TYPE) { column: Int, rs: ResultSet ->
        rs.getLong(column).asSuccess()
    }

private val BIG_SERIAL_TYPE = ColumnTypes("bigserial")
