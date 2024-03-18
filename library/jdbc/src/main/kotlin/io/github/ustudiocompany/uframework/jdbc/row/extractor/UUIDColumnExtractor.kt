package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.flatMap
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.row.Row
import java.sql.SQLException
import java.util.*

public fun Row.getUUID(index: Int): Result<UUID?, JDBCErrors> =
    UUIDColumnExtractor.extract(this, index)

public fun Row.getUUID(columnName: String): Result<UUID?, JDBCErrors> =
    UUIDColumnExtractor.extract(this, columnName)

private object UUIDColumnExtractor : Row.ColumnValueExtractor<UUID>() {

    override fun extract(row: Row, index: Int): Result<UUID?, JDBCErrors> =
        try {
            val resultSet = resultSet(row)
            val result = resultSet.getObject(index, UUID::class.java)
            if (resultSet.wasNull()) Result.asNull else result.success()
        } catch (expected: ClassCastException) {
            JDBCErrors.Row.ReadColumn(index, expected).error()
        } catch (expected: ArrayIndexOutOfBoundsException) {
            JDBCErrors.Rows.UndefinedColumn(index, expected).error()
        } catch (expected: SQLException) {
            val error = if (expected.isConnectionError)
                JDBCErrors.Connection(expected)
            else
                JDBCErrors.UnexpectedError(expected)
            error.error()
        } catch (expected: Exception) {
            JDBCErrors.UnexpectedError(expected).error()
        }

    override fun extract(row: Row, columnName: String): Result<UUID?, JDBCErrors> =
        try {
            findColumnIndex(row, columnName)
                .flatMap { index ->
                    val resultSet = resultSet(row)
                    val result = resultSet.getObject(index, UUID::class.java)
                    if (resultSet.wasNull()) Result.asNull else result.success()
                }
        } catch (expected: ClassCastException) {
            JDBCErrors.Row.ReadColumn(columnName, expected).error()
        } catch (expected: SQLException) {
            val error = if (expected.isConnectionError)
                JDBCErrors.Connection(expected)
            else
                JDBCErrors.UnexpectedError(expected)
            error.error()
        } catch (expected: Exception) {
            JDBCErrors.UnexpectedError(expected).error()
        }
}
