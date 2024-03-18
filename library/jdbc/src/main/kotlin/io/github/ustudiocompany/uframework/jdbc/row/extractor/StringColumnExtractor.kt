package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.flatMap
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.exception.isInvalidColumnIndex
import io.github.ustudiocompany.uframework.jdbc.row.Row
import org.postgresql.util.PSQLState
import java.sql.SQLException

public fun Row.getString(index: Int): Result<String?, JDBCErrors> =
    StringColumnExtractor.extract(this, index)

public fun Row.getString(columnName: String): Result<String?, JDBCErrors> =
    StringColumnExtractor.extract(this, columnName)

private object StringColumnExtractor : Row.ColumnValueExtractor<String>() {

    override fun extract(row: Row, index: Int): Result<String?, JDBCErrors> {
        fun SQLException.isReadingError() =
            sqlState == PSQLState.DATA_TYPE_MISMATCH.state ||
                sqlState == PSQLState.NUMERIC_VALUE_OUT_OF_RANGE.state

        return try {
            val resultSet = resultSet(row)
            val result = resultSet.getString(index)
            if (resultSet.wasNull()) Result.asNull else result.success()
        } catch (expected: SQLException) {
            val error = when {
                expected.isConnectionError -> JDBCErrors.Connection(expected)
                expected.isInvalidColumnIndex -> JDBCErrors.Rows.UndefinedColumn(index, expected)
                expected.isReadingError() -> JDBCErrors.Row.ReadColumn(index, expected)
                else -> JDBCErrors.UnexpectedError(expected)
            }
            error.error()
        } catch (expected: Exception) {
            JDBCErrors.UnexpectedError(expected).error()
        }
    }

    override fun extract(row: Row, columnName: String): Result<String?, JDBCErrors> {
        fun SQLException.isReadingError() =
            sqlState == PSQLState.DATA_TYPE_MISMATCH.state ||
                sqlState == PSQLState.NUMERIC_VALUE_OUT_OF_RANGE.state

        return try {
            findColumnIndex(row, columnName)
                .flatMap { index ->
                    val resultSet = resultSet(row)
                    val result = resultSet.getString(index)
                    if (resultSet.wasNull()) Result.asNull else result.success()
                }
        } catch (expected: SQLException) {
            val error = when {
                expected.isConnectionError -> JDBCErrors.Connection(expected)
                expected.isReadingError() -> JDBCErrors.Row.ReadColumn(columnName, expected)
                else -> JDBCErrors.UnexpectedError(expected)
            }
            error.error()
        } catch (expected: Exception) {
            JDBCErrors.UnexpectedError(expected).error()
        }
    }
}
