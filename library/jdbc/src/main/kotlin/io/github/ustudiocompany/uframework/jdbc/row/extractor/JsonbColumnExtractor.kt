package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.ResultK
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import org.postgresql.util.PGobject

public fun Row.getJsonb(index: Int): ResultK<String?, JDBCErrors> =
    JsonbColumnExtractor.extract(this, index)

public fun Row.getJsonb(columnName: String): ResultK<String?, JDBCErrors> =
    JsonbColumnExtractor.extract(this, columnName)

private object JsonbColumnExtractor : Row.ColumnValueExtractor<String>(
    ExpectedTypes("jsonb")
) {

    override fun extract(row: Row, index: Int): ResultK<String?, JDBCErrors> =
        row.extract(index) {
            getObject(it, PGobject::class.java).value
        }

    override fun extract(row: Row, columnName: String): ResultK<String?, JDBCErrors> =
        row.extract(columnName) {
            getObject(it, PGobject::class.java).value
        }
}
