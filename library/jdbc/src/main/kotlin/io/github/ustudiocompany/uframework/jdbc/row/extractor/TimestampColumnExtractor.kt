package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.Timestamp

public fun Row.getTimestamp(index: Int): ResultK<Timestamp?, JDBCErrors> =
    TimestampColumnExtractor.extract(this, index)

public fun Row.getTimestamp(columnName: String): ResultK<Timestamp?, JDBCErrors> =
    TimestampColumnExtractor.extract(this, columnName)

private object TimestampColumnExtractor : Row.ColumnValueExtractor<Timestamp>(
    ExpectedTypes("timestamp")
) {

    override fun extract(row: Row, index: Int): ResultK<Timestamp?, JDBCErrors> =
        row.extract(index) { getTimestamp(it) }

    override fun extract(row: Row, columnName: String): ResultK<Timestamp?, JDBCErrors> =
        row.extract(columnName) { getTimestamp(it) }
}
