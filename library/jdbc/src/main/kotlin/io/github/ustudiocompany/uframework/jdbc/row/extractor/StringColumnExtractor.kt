package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.Types

public fun Row.getString(index: Int): Result<String?, JDBCErrors> =
    StringColumnExtractor.extract(this, index)

public fun Row.getString(columnName: String): Result<String?, JDBCErrors> =
    StringColumnExtractor.extract(this, columnName)

private object StringColumnExtractor : Row.ColumnValueExtractor<String>(
    ExpectedType("TEXT/VARCHAR/CHAR", Types.VARCHAR, Types.CHAR)
) {

    override fun extract(row: Row, index: Int): Result<String?, JDBCErrors> =
        row.extract(index) { getString(it) }

    override fun extract(row: Row, columnName: String): Result<String?, JDBCErrors> =
        row.extract(columnName) { getString(it) }
}
