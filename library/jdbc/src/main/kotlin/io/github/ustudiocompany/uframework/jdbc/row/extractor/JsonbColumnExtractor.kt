package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import org.postgresql.util.PGobject
import java.sql.Types

public fun Row.getJsonb(index: Int): Result<String?, JDBCErrors> =
    JsonbColumnExtractor.extract(this, index)

public fun Row.getJsonb(columnName: String): Result<String?, JDBCErrors> =
    JsonbColumnExtractor.extract(this, columnName)

private object JsonbColumnExtractor : Row.ColumnValueExtractor<String>(
    ExpectedType("Json/Jsonb", Types.OTHER)
) {

    override fun extract(row: Row, index: Int): Result<String?, JDBCErrors> =
        row.extract(index) { getObject(it, PGobject::class.java).value }

    override fun extract(row: Row, columnName: String): Result<String?, JDBCErrors> =
        row.extract(columnName) { getObject(it, PGobject::class.java).value }
}
