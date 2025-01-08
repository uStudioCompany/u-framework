package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.exception.isUndefinedColumn
import io.github.ustudiocompany.uframework.jdbc.row.Row.ExpectedTypes
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException

internal class RowsInstance(private val resultSet: ResultSet) : Rows, Row {

    override fun iterator(): Iterator<Row> = ResultSetIterator()

    override fun <T> extract(
        index: Int,
        expectedTypes: ExpectedTypes,
        extractor: ResultSet.(index: Int) -> T
    ): ResultK<T?, JDBCErrors> = try {
        val metadata = resultSet.metaData
        metadata.checkIndex(index = index).getOrForward { return it }
        metadata.checkType(index = index, expectedTypes).getOrForward { return it }
        val result = extractor(resultSet, index)
        if (resultSet.wasNull()) Success.asNull else result.asSuccess()
    } catch (expected: ClassCastException) {
        JDBCErrors.Row.ReadColumn(index, expected).asFailure()
    } catch (expected: SQLException) {
        val error = if (expected.isConnectionError)
            JDBCErrors.Connection(expected)
        else
            JDBCErrors.Unexpected(expected)
        error.asFailure()
    } catch (expected: Exception) {
        JDBCErrors.Unexpected(expected).asFailure()
    }

    override fun <T> extract(
        columnName: String,
        expectedTypes: ExpectedTypes,
        extractor: ResultSet.(Int) -> T
    ): ResultK<T?, JDBCErrors> = try {
        resultSet.getColumnIndexOrNull(name = columnName)
            ?.let { index ->
                val metadata = resultSet.metaData
                metadata.checkType(index = index, columnName = columnName, expectedTypes).getOrForward { return it }
                val result = extractor(resultSet, index)
                if (resultSet.wasNull()) Success.asNull else result.asSuccess()
            }
            ?: JDBCErrors.Row.UndefinedColumn(columnName).asFailure()
    } catch (expected: ClassCastException) {
        JDBCErrors.Row.ReadColumn(columnName, expected).asFailure()
    } catch (expected: SQLException) {
        val error = if (expected.isConnectionError)
            JDBCErrors.Connection(expected)
        else
            JDBCErrors.Unexpected(expected)
        error.asFailure()
    } catch (expected: Exception) {
        JDBCErrors.Unexpected(expected).asFailure()
    }

    private fun ResultSet.getColumnIndexOrNull(name: String): Int? = try {
        findColumn(name)
    } catch (expected: SQLException) {
        if (expected.isUndefinedColumn) null else throw expected
    }

    private fun ResultSetMetaData.checkIndex(index: Int): ResultK<Unit, JDBCErrors> =
        if (index < 1 || index > columnCount)
            JDBCErrors.Row.UndefinedColumn(index).asFailure()
        else
            Success.asUnit

    private fun ResultSetMetaData.checkType(index: Int, expectedTypes: ExpectedTypes): ResultK<Unit, JDBCErrors> {
        val actualType = getColumnTypeName(index)
        return if (actualType in expectedTypes)
            Success.asUnit
        else
            JDBCErrors.Row.TypeMismatch(index, expectedTypes.toString(), actualType).asFailure()
    }

    private fun ResultSetMetaData.checkType(
        index: Int,
        columnName: String,
        expectedTypes: ExpectedTypes
    ): ResultK<Unit, JDBCErrors> {
        val actualType = getColumnTypeName(index)
        return if (actualType in expectedTypes)
            Success.asUnit
        else
            JDBCErrors.Row.TypeMismatch(columnName, expectedTypes.toString(), actualType).asFailure()
    }

    private inner class ResultSetIterator : AbstractIterator<Row>() {
        override fun computeNext() {
            if (resultSet.next())
                setNext(this@RowsInstance)
            else
                done()
        }
    }
}
