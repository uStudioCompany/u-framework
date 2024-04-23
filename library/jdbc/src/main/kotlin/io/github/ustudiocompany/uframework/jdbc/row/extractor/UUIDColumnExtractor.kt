package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.success
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
        row.extractObject(index) {
            if (it.type.equals(UUID_TYPE, true))
                if (it.value != null) UUID.fromString(it.value).success() else Result.asNull
            else
                JDBCErrors.Row.TypeMismatch(index, UUID_TYPE, it.type).failure()
        }

    override fun extract(row: Row, columnName: String): Result<UUID?, JDBCErrors> =
        row.extractObject(columnName) {
            if (it.type.equals(UUID_TYPE, true))
                if (it.value != null) UUID.fromString(it.value).success() else Result.asNull
            else
                JDBCErrors.Row.TypeMismatch(columnName, UUID_TYPE, it.type).failure()
        }
}
