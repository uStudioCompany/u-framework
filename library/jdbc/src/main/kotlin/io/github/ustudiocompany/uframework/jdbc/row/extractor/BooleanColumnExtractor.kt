package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row

public fun Row.getBoolean(index: Int): ResultK<Boolean?, JDBCErrors> =
    Extractor.extract(this, index)

public fun Row.getBoolean(columnName: String): ResultK<Boolean?, JDBCErrors> =
    Extractor.extract(this, columnName)

private object Extractor : Row.ColumnValueExtractor<Boolean>(
    ExpectedTypes("bool")
) {

    override fun extract(row: Row, index: Int): ResultK<Boolean?, JDBCErrors> =
        row.extract(index) { getBoolean(it) }

    override fun extract(row: Row, columnName: String): ResultK<Boolean?, JDBCErrors> =
        row.extract(columnName) { getBoolean(it) }
}
