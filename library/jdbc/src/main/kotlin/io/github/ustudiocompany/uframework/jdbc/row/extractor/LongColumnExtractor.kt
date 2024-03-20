package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.Types

public fun Row.getLong(index: Int): Result<Long?, JDBCErrors> =
    LongColumnExtractor.extract(this, index)

public fun Row.getLong(columnName: String): Result<Long?, JDBCErrors> =
    LongColumnExtractor.extract(this, columnName)

private object LongColumnExtractor : Row.ColumnValueExtractor<Long>(
    ExpectedType("BIGINT", Types.BIGINT)
) {

    override fun extract(row: Row, index: Int): Result<Long?, JDBCErrors> =
        row.extract(index) { getLong(it) }

    override fun extract(row: Row, columnName: String): Result<Long?, JDBCErrors> =
        row.extract(columnName) { getLong(it) }
}
