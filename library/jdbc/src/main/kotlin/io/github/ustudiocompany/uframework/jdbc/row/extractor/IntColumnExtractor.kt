package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row

public fun Row.getInt(index: Int): ResultK<Int?, JDBCErrors> =
    IntColumnExtractor.extract(this, index)

public fun Row.getInt(columnName: String): ResultK<Int?, JDBCErrors> =
    IntColumnExtractor.extract(this, columnName)

private object IntColumnExtractor : Row.ColumnValueExtractor<Int>(
    ExpectedTypes("int4")
) {

    override fun extract(row: Row, index: Int): ResultK<Int?, JDBCErrors> =
        row.extract(index) { getInt(it) }

    override fun extract(row: Row, columnName: String): ResultK<Int?, JDBCErrors> =
        row.extract(columnName) { getInt(it) }
}
