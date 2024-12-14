package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
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

        public abstract fun extract(row: Row, index: Int): ResultK<T?, JDBCErrors>
        public abstract fun extract(row: Row, columnName: String): ResultK<T?, JDBCErrors>

        protected fun resultSet(row: Row): ResultSet = row.resultSet
        protected inline fun <T> Row.extract(index: Int, block: ResultSet.(Int) -> T): ResultK<T?, JDBCErrors> =
            try {
                val resultSet = resultSet(this)
                val metadata = resultSet.metaData
                metadata.checkIndex(index).getOrForward { return it }
                metadata.checkType(index).getOrForward { return it }
                val result = block(resultSet, index)
                if (resultSet.wasNull()) Success.asNull else result.asSuccess()
            } catch (expected: ClassCastException) {
                JDBCErrors.Row.ReadColumn(index, expected).asFailure()
            } catch (expected: SQLException) {
                val error = if (expected.isConnectionError)
                    JDBCErrors.Connection(expected)
                else
                    JDBCErrors.UnexpectedError(expected)
                error.asFailure()
            } catch (expected: Exception) {
                JDBCErrors.UnexpectedError(expected).asFailure()
            }

        protected inline fun <T> Row.extract(columnName: String, block: ResultSet.(Int) -> T): ResultK<T?, JDBCErrors> =
            try {
                val resultSet = resultSet(this)
                resultSet.getColumnIndexOrNull(columnName)
                    ?.let { index ->
                        val metadata = resultSet.metaData
                        metadata.checkType(index, columnName).getOrForward { return it }
                        val result = block(resultSet, index)
                        if (resultSet.wasNull()) Success.asNull else result.asSuccess()
                    }
                    ?: JDBCErrors.Row.UndefinedColumn(columnName).asFailure()
            } catch (expected: ClassCastException) {
                JDBCErrors.Row.ReadColumn(columnName, expected).asFailure()
            } catch (expected: SQLException) {
                val error = if (expected.isConnectionError)
                    JDBCErrors.Connection(expected)
                else
                    JDBCErrors.UnexpectedError(expected)
                error.asFailure()
            } catch (expected: Exception) {
                JDBCErrors.UnexpectedError(expected).asFailure()
            }

        protected fun ResultSet.getColumnIndexOrNull(columnName: String): Int? = try {
            findColumn(columnName)
        } catch (expected: SQLException) {
            if (expected.isUndefinedColumn) null else throw expected
        }

        protected fun ResultSetMetaData.checkIndex(index: Int): ResultK<Unit, JDBCErrors> =
            if (index < 1 || index > columnCount)
                JDBCErrors.Row.UndefinedColumn(index).asFailure()
            else
                Success.asUnit

        protected fun ResultSetMetaData.checkType(index: Int): ResultK<Unit, JDBCErrors> {
            val actualType = getColumnTypeName(index)
            return if (actualType in expectedTypes)
                Success.asUnit
            else
                JDBCErrors.Row.TypeMismatch(index, expectedTypes.toString(), actualType).asFailure()
        }

        protected fun ResultSetMetaData.checkType(index: Int, columnName: String): ResultK<Unit, JDBCErrors> {
            val actualType = getColumnTypeName(index)
            return if (actualType in expectedTypes)
                Success.asUnit
            else
                JDBCErrors.Row.TypeMismatch(columnName, expectedTypes.toString(), actualType).asFailure()
        }

        protected fun PGobject.obtainValue(columnType: String, columnIndex: Int): ResultK<String?, JDBCErrors> =
            if (type.equals(columnType, true))
                if (value != null) value.asSuccess() else Success.asNull
            else
                JDBCErrors.Row.TypeMismatch(columnIndex, columnType, type).asFailure()

        protected fun PGobject.obtainValue(columnType: String, columnName: String): ResultK<String?, JDBCErrors> =
            if (type.equals(columnType, true))
                if (value != null) value.asSuccess() else Success.asNull
            else
                JDBCErrors.Row.TypeMismatch(columnName, columnType, type).asFailure()

        @JvmInline
        public value class ExpectedTypes(private val names: List<String>) {
            public constructor(vararg name: String) : this(name.toList())

            public operator fun contains(type: String): Boolean = names.any { it.equals(type, true) }

            override fun toString(): String = names.joinToString(", ")
        }
    }
}
