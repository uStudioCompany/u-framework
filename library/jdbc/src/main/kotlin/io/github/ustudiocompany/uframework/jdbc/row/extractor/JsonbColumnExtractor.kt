package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.Types

public fun Row.getJsonb(index: Int): Result<String?, JDBCErrors> =
    JsonbColumnExtractor.extract(this, index)

public fun Row.getJsonb(columnName: String): Result<String?, JDBCErrors> =
    JsonbColumnExtractor.extract(this, columnName)

private object JsonbColumnExtractor : Row.ColumnValueExtractor<String>(
    ExpectedType("Json/Jsonb", Types.OTHER)
) {

    private const val JSONB_TYPE = "jsonb"

    override fun extract(row: Row, index: Int): Result<String?, JDBCErrors> =
        row.extractObject(index) {
            it.obtainValue(JSONB_TYPE, index)
        }

    override fun extract(row: Row, columnName: String): Result<String?, JDBCErrors> =
        row.extractObject(columnName) {
            it.obtainValue(JSONB_TYPE, columnName)
        }
}
