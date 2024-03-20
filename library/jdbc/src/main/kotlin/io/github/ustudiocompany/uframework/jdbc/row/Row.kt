package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.flatMap
import io.github.airflux.functional.getOrForward
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.exception.isUndefinedColumn
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException

@JvmInline
public value class Row(private val resultSet: ResultSet) {

    public abstract class ColumnValueExtractor<T>(protected val expectedType: ExpectedType) {

        public abstract fun extract(row: Row, index: Int): Result<T?, JDBCErrors>
        public abstract fun extract(row: Row, columnName: String): Result<T?, JDBCErrors>

        protected inline fun <T> Row.extract(index: Int, block: ResultSet.(Int) -> T): Result<T?, JDBCErrors> =
            try {
                val resultSet = resultSet(this)
                val metadata = resultSet.metaData
                metadata.checkIndex(index).getOrForward { return it }
                metadata.checkType(index).getOrForward { return it }
                val result = block(resultSet, index)
                if (resultSet.wasNull()) Result.asNull else result.success()
            } catch (expected: ClassCastException) {
                JDBCErrors.Row.ReadColumn(index, expected).error()
            } catch (expected: SQLException) {
                val error = if (expected.isConnectionError)
                    JDBCErrors.Connection(expected)
                else
                    JDBCErrors.UnexpectedError(expected)
                error.error()
            } catch (expected: Exception) {
                JDBCErrors.UnexpectedError(expected).error()
            }

        protected inline fun <T> Row.extract(columnName: String, block: ResultSet.(Int) -> T): Result<T?, JDBCErrors> =
            try {
                findColumnIndex(this, columnName)
                    .flatMap { index ->
                        val resultSet = resultSet(this)
                        val metadata = resultSet.metaData
                        metadata.checkType(index, columnName).getOrForward { return it }
                        val result = block(resultSet, index)
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

        protected fun resultSet(row: Row): ResultSet = row.resultSet

        protected fun findColumnIndex(row: Row, name: String): Result<Int, JDBCErrors> = try {
            row.resultSet.findColumn(name).success()
        } catch (expected: SQLException) {
            val error = when {
                expected.isConnectionError -> JDBCErrors.Connection(expected)
                expected.isUndefinedColumn -> JDBCErrors.Row.UndefinedColumn(name)
                else -> JDBCErrors.UnexpectedError(expected)
            }
            error.error()
        }

        protected fun ResultSetMetaData.checkIndex(index: Int): Result<Unit, JDBCErrors> =
            if (index < 1 || index > columnCount)
                JDBCErrors.Row.UndefinedColumn(index).error()
            else
                Result.asUnit

        protected fun ResultSetMetaData.checkType(index: Int): Result<Unit, JDBCErrors> {
            val actualType = getColumnType(index)
            return if (actualType !in expectedType.codes)
                JDBCErrors.Row.TypeMismatch(index, expectedType.name, actualType).error()
            else
                Result.asUnit
        }

        protected fun ResultSetMetaData.checkType(index: Int, columnName: String): Result<Unit, JDBCErrors> {
            val actualType = getColumnType(index)
            return if (actualType !in expectedType.codes)
                JDBCErrors.Row.TypeMismatch(columnName, expectedType.name, actualType).error()
            else
                Result.asUnit
        }

        public class ExpectedType(public val name: String, public val codes: List<Int>) {
            public constructor(name: String, vararg types: Int) : this(name, types.toList())
        }
    }
}
