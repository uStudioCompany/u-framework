package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.result.Result
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.util.*

public fun Row.getUUID(index: Int): Result<UUID?, JDBCErrors> =
    UUIDColumnExtractor.extract(this, index)

public fun Row.getUUID(columnName: String): Result<UUID?, JDBCErrors> =
    UUIDColumnExtractor.extract(this, columnName)

private object UUIDColumnExtractor : Row.ColumnValueExtractor<UUID>(
    ExpectedTypes("uuid")
) {

    override fun extract(row: Row, index: Int): Result<UUID?, JDBCErrors> =
        row.extract(index) {
            getObject(it, UUID::class.java)
        }

    override fun extract(row: Row, columnName: String): Result<UUID?, JDBCErrors> =
        row.extract(columnName) {
            getObject(it, UUID::class.java)
        }
}
