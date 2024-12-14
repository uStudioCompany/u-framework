package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row

public fun Row.getLong(index: Int): ResultK<Long?, JDBCErrors> =
    LongColumnExtractor.extract(this, index)

public fun Row.getLong(columnName: String): ResultK<Long?, JDBCErrors> =
    LongColumnExtractor.extract(this, columnName)

private object LongColumnExtractor : Row.ColumnValueExtractor<Long>(
    ExpectedTypes("int8")
) {

    override fun extract(row: Row, index: Int): ResultK<Long?, JDBCErrors> =
        row.extract(index) { getLong(it) }

    override fun extract(row: Row, columnName: String): ResultK<Long?, JDBCErrors> =
        row.extract(columnName) { getLong(it) }
}
