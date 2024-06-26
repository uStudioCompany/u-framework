package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.result.Result
import io.github.airflux.commons.types.result.failure
import io.github.airflux.commons.types.result.getOrForward
import io.github.airflux.commons.types.result.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.exception.isUndefinedColumn
import org.postgresql.util.PGobject
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException

@JvmInline
@Suppress("TooManyFunctions")
public value class Row(private val resultSet: ResultSet) {

    public abstract class ColumnValueExtractor<T>(protected val expectedTypes: ExpectedTypes) {

        public abstract fun extract(row: Row, index: Int): Result<T?, JDBCErrors>
        public abstract fun extract(row: Row, columnName: String): Result<T?, JDBCErrors>

        protected fun resultSet(row: Row): ResultSet = row.resultSet
        protected inline fun <T> Row.extract(index: Int, block: ResultSet.(Int) -> T): Result<T?, JDBCErrors> =
            try {
                val resultSet = resultSet(this)
                val metadata = resultSet.metaData
                metadata.checkIndex(index).getOrForward { return it }
                metadata.checkType(index).getOrForward { return it }
                val result = block(resultSet, index)
                if (resultSet.wasNull()) Result.asNull else result.success()
            } catch (expected: ClassCastException) {
                JDBCErrors.Row.ReadColumn(index, expected).failure()
            } catch (expected: SQLException) {
                val error = if (expected.isConnectionError)
                    JDBCErrors.Connection(expected)
                else
                    JDBCErrors.UnexpectedError(expected)
                error.failure()
            } catch (expected: Exception) {
                JDBCErrors.UnexpectedError(expected).failure()
            }

        protected inline fun <T> Row.extract(columnName: String, block: ResultSet.(Int) -> T): Result<T?, JDBCErrors> =
            try {
                val resultSet = resultSet(this)
                resultSet.getColumnIndexOrNull(columnName)
                    ?.let { index ->
                        val metadata = resultSet.metaData
                        metadata.checkType(index, columnName).getOrForward { return it }
                        val result = block(resultSet, index)
                        if (resultSet.wasNull()) Result.asNull else result.success()
                    }
                    ?: JDBCErrors.Row.UndefinedColumn(columnName).failure()
            } catch (expected: ClassCastException) {
                JDBCErrors.Row.ReadColumn(columnName, expected).failure()
            } catch (expected: SQLException) {
                val error = if (expected.isConnectionError)
                    JDBCErrors.Connection(expected)
                else
                    JDBCErrors.UnexpectedError(expected)
                error.failure()
            } catch (expected: Exception) {
                JDBCErrors.UnexpectedError(expected).failure()
            }

        protected fun ResultSet.getColumnIndexOrNull(columnName: String): Int? = try {
            findColumn(columnName)
        } catch (expected: SQLException) {
            if (expected.isUndefinedColumn) null else throw expected
        }

        protected fun ResultSetMetaData.checkIndex(index: Int): Result<Unit, JDBCErrors> =
            if (index < 1 || index > columnCount)
                JDBCErrors.Row.UndefinedColumn(index).failure()
            else
                Result.asUnit

        protected fun ResultSetMetaData.checkType(index: Int): Result<Unit, JDBCErrors> {
            val actualType = getColumnTypeName(index)
            return if (actualType in expectedTypes)
                Result.asUnit
            else
                JDBCErrors.Row.TypeMismatch(index, expectedTypes.toString(), actualType).failure()
        }

        protected fun ResultSetMetaData.checkType(index: Int, columnName: String): Result<Unit, JDBCErrors> {
            val actualType = getColumnTypeName(index)
            return if (actualType in expectedTypes)
                Result.asUnit
            else
                JDBCErrors.Row.TypeMismatch(columnName, expectedTypes.toString(), actualType).failure()
        }

        protected fun PGobject.obtainValue(columnType: String, columnIndex: Int): Result<String?, JDBCErrors> =
            if (type.equals(columnType, true))
                if (value != null) value.success() else Result.asNull
            else
                JDBCErrors.Row.TypeMismatch(columnIndex, columnType, type).failure()

        protected fun PGobject.obtainValue(columnType: String, columnName: String): Result<String?, JDBCErrors> =
            if (type.equals(columnType, true))
                if (value != null) value.success() else Result.asNull
            else
                JDBCErrors.Row.TypeMismatch(columnName, columnType, type).failure()

        @JvmInline
        public value class ExpectedTypes(private val names: List<String>) {
            public constructor(vararg name: String) : this(name.toList())

            public operator fun contains(type: String): Boolean = names.any { it.equals(type, true) }

            override fun toString(): String = names.joinToString(", ")
        }
    }
}
