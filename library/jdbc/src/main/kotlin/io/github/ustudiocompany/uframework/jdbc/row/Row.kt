package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.flatMap
import io.github.airflux.functional.map
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.exception.isInvalidColumnIndex
import io.github.ustudiocompany.uframework.jdbc.exception.isUndefinedColumn
import org.postgresql.util.PSQLState
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

@Suppress("TooManyFunctions")
public class Row internal constructor(private val resultSet: ResultSet) {

    public fun getBoolean(index: Int): Result<Boolean, JDBCErrors> {
        fun SQLException.isReadingError() =
            sqlState == PSQLState.DATA_TYPE_MISMATCH.state ||
                sqlState == PSQLState.CANNOT_COERCE.state

        return try {
            resultSet.getBoolean(index).success()
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

    public fun getBoolean(columnName: String): Result<Boolean, JDBCErrors> {
        fun SQLException.isReadingError() =
            sqlState == PSQLState.DATA_TYPE_MISMATCH.state ||
                sqlState == PSQLState.CANNOT_COERCE.state

        return try {
            findColumnIndex(columnName).map { index -> resultSet.getBoolean(index) }
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

    public fun getString(index: Int): Result<String, JDBCErrors> {
        return try {
            resultSet.getString(index).success()
        } catch (expected: SQLException) {
            val error = when {
                expected.isConnectionError -> JDBCErrors.Connection(expected)
                expected.isInvalidColumnIndex -> JDBCErrors.Rows.UndefinedColumn(index, expected)
                else -> JDBCErrors.UnexpectedError(expected)
            }
            error.error()
        } catch (expected: Exception) {
            JDBCErrors.UnexpectedError(expected).error()
        }
    }

    public fun getString(columnName: String): Result<String, JDBCErrors> {
        return try {
            findColumnIndex(columnName)
                .flatMap { index -> resultSet.getString(index).success() }
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

    public fun getInt(index: Int): Result<Int, JDBCErrors> {
        fun SQLException.isReadingError() =
            sqlState == PSQLState.DATA_TYPE_MISMATCH.state ||
                sqlState == PSQLState.NUMERIC_VALUE_OUT_OF_RANGE.state

        return try {
            resultSet.getInt(index).success()
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

    public fun getInt(columnName: String): Result<Int, JDBCErrors> {
        fun SQLException.isReadingError() =
            sqlState == PSQLState.DATA_TYPE_MISMATCH.state ||
                sqlState == PSQLState.NUMERIC_VALUE_OUT_OF_RANGE.state

        return try {
            findColumnIndex(columnName)
                .flatMap { index -> resultSet.getInt(index).success() }
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

    public fun getLong(index: Int): Result<Long, JDBCErrors> {
        fun SQLException.isReadingError() =
            sqlState == PSQLState.DATA_TYPE_MISMATCH.state ||
                sqlState == PSQLState.NUMERIC_VALUE_OUT_OF_RANGE.state

        return try {
            resultSet.getLong(index).success()
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

    public fun getLong(columnName: String): Result<Long, JDBCErrors> {
        fun SQLException.isReadingError() =
            sqlState == PSQLState.DATA_TYPE_MISMATCH.state ||
                sqlState == PSQLState.NUMERIC_VALUE_OUT_OF_RANGE.state

        return try {
            findColumnIndex(columnName)
                .flatMap { index -> resultSet.getLong(index).success() }
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

    public fun getUUID(index: Int): Result<UUID, JDBCErrors> =
        try {
            resultSet.getObject(index, UUID::class.java).success()
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

    public fun getUUID(columnName: String): Result<UUID, JDBCErrors> {
        return try {
            findColumnIndex(columnName)
                .flatMap { index -> resultSet.getObject(index, UUID::class.java).success() }
        } catch (expected: ClassCastException) {
            JDBCErrors.Row.ReadColumn(columnName, expected).error()
        } catch (expected: SQLException) {
            val error = when {
                expected.isConnectionError -> JDBCErrors.Connection(expected)
                expected.isInvalidColumnIndex -> JDBCErrors.Rows.UndefinedColumn(columnName, expected)
                else -> JDBCErrors.UnexpectedError(expected)
            }
            error.error()
        } catch (expected: Exception) {
            JDBCErrors.UnexpectedError(expected).error()
        }
    }

    private fun findColumnIndex(name: String): Result<Int, JDBCErrors> = try {
        resultSet.findColumn(name).success()
    } catch (expected: SQLException) {
        val error = when {
            expected.isConnectionError -> JDBCErrors.Connection(expected)
            expected.isUndefinedColumn -> JDBCErrors.Rows.UndefinedColumn(name, expected)
            else -> JDBCErrors.UnexpectedError(expected)
        }
        error.error()
    }
}
