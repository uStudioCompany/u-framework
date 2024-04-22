package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.Types

public fun Row.getBoolean(index: Int): Result<Boolean?, JDBCErrors> =
    Extractor.extract(this, index)

public fun Row.getBoolean(columnName: String): Result<Boolean?, JDBCErrors> =
    Extractor.extract(this, columnName)

private object Extractor : Row.ColumnValueExtractor<Boolean>(
    ExpectedType("BOOLEAN", Types.BOOLEAN, Types.BIT)
) {

    override fun extract(row: Row, index: Int): Result<Boolean?, JDBCErrors> =
        row.extract(index) { getBoolean(it) }

    override fun extract(row: Row, columnName: String): Result<Boolean?, JDBCErrors> =
        row.extract(columnName) { getBoolean(it) }
}
