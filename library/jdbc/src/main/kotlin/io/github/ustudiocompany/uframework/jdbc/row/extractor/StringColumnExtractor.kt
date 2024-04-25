package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row

public fun Row.getString(index: Int): Result<String?, JDBCErrors> =
    StringColumnExtractor.extract(this, index)

public fun Row.getString(columnName: String): Result<String?, JDBCErrors> =
    StringColumnExtractor.extract(this, columnName)

private object StringColumnExtractor : Row.ColumnValueExtractor<String>(
    ExpectedTypes("text", "varchar", "bpchar")
) {

    override fun extract(row: Row, index: Int): Result<String?, JDBCErrors> =
        row.extract(index) { getString(it) }

    override fun extract(row: Row, columnName: String): Result<String?, JDBCErrors> =
        row.extract(columnName) { getString(it) }
}
