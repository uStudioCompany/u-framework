package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.functional.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import org.postgresql.util.PGobject
import java.sql.Types

public fun Row.getJson(index: Int): Result<String?, JDBCErrors> =
    JsonColumnExtractor.extract(this, index)

public fun Row.getJson(columnName: String): Result<String?, JDBCErrors> =
    JsonColumnExtractor.extract(this, columnName)

private object JsonColumnExtractor : Row.ColumnValueExtractor<String>(
    ExpectedType("Json/Jsonb", Types.OTHER)
) {

    override fun extract(row: Row, index: Int): Result<String?, JDBCErrors> =
        row.extract(index) { getObject(it, PGobject::class.java).value }

    override fun extract(row: Row, columnName: String): Result<String?, JDBCErrors> =
        row.extract(columnName) { getObject(it, PGobject::class.java).value }
}
