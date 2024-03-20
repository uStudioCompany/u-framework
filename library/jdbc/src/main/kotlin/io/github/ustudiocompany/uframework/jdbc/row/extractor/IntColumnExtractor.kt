package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.Types

public fun Row.getInt(index: Int): Result<Int?, JDBCErrors> =
    IntColumnExtractor.extract(this, index)

public fun Row.getInt(columnName: String): Result<Int?, JDBCErrors> =
    IntColumnExtractor.extract(this, columnName)

private object IntColumnExtractor : Row.ColumnValueExtractor<Int>(
    ExpectedType("INT", Types.INTEGER)
) {

    override fun extract(row: Row, index: Int): Result<Int?, JDBCErrors> =
        row.extract(index) { getInt(it) }

    override fun extract(row: Row, columnName: String): Result<Int?, JDBCErrors> =
        row.extract(columnName) { getInt(it) }
}
