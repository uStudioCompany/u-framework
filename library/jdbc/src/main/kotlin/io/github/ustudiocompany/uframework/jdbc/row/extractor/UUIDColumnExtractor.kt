package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.map
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.Types
import java.util.*

public fun Row.getUUID(index: Int): Result<UUID?, JDBCErrors> =
    UUIDColumnExtractor.extract(this, index)

public fun Row.getUUID(columnName: String): Result<UUID?, JDBCErrors> =
    UUIDColumnExtractor.extract(this, columnName)

private object UUIDColumnExtractor : Row.ColumnValueExtractor<UUID>(
    ExpectedType("UUID", Types.JAVA_OBJECT, Types.OTHER)
) {

    private const val UUID_TYPE = "uuid"

    override fun extract(row: Row, index: Int): Result<UUID?, JDBCErrors> =
        row.extractObject(index) { pGobject ->
            pGobject.obtainValue(UUID_TYPE, index)
                .map { it?.let { UUID.fromString(it) } }
        }

    override fun extract(row: Row, columnName: String): Result<UUID?, JDBCErrors> =
        row.extractObject(columnName) { pGobject ->
            pGobject.obtainValue(UUID_TYPE, columnName)
                .map { it?.let { UUID.fromString(it) } }
        }
}
