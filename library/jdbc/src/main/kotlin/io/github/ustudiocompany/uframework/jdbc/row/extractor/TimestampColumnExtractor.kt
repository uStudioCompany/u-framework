package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.Timestamp
import java.sql.Types

public fun Row.getTimestamp(index: Int): Result<Timestamp?, JDBCErrors> =
    TimestampColumnExtractor.extract(this, index)

public fun Row.getTimestamp(columnName: String): Result<Timestamp?, JDBCErrors> =
    TimestampColumnExtractor.extract(this, columnName)

private object TimestampColumnExtractor : Row.ColumnValueExtractor<Timestamp>(
    ExpectedType("TIMESTAMP", Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE)
) {

    override fun extract(row: Row, index: Int): Result<Timestamp?, JDBCErrors> =
        row.extract(index) { getTimestamp(it) }

    override fun extract(row: Row, columnName: String): Result<Timestamp?, JDBCErrors> =
        row.extract(columnName) { getTimestamp(it) }
}
