package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.jdbc.JDBCFail
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.jdbcError
import io.github.ustudiocompany.uframework.jdbc.row.extractor.DataExtractorWith
import java.sql.ResultSet
import java.sql.ResultSetMetaData

internal class ResultRowsInstance(
    private val resultSet: ResultSet,
) : ResultRows, ResultRow {

    override fun <T> extractWith(index: Int, types: ResultRow.Types, block: DataExtractorWith<T>): JDBCResult<T?> =
        try {
            val metadata = resultSet.metaData
            metadata.checkIndex(index = index).getOrForward { return it }
            metadata.checkType(index = index, types).getOrForward { return it }
            block(index, resultSet)
        } catch (expected: Exception) {
            jdbcError(
                description = "Error while extracting data from the result set",
                exception = expected
            )
        }

    override fun iterator(): Iterator<ResultRow> = ResultSetIterator()

    private fun ResultSetMetaData.checkIndex(index: Int): JDBCFail =
        if (index < 1 || index > columnCount)
            jdbcError(description = "The column index '$index' is out of bounds.")
        else
            Success.asUnit

    private fun ResultSetMetaData.checkType(index: Int, types: ResultRow.Types): JDBCFail {
        val actualType = getColumnTypeName(index)
        return if (actualType in types)
            Success.asUnit
        else
            jdbcError(
                description = "The column type with index '$index' does not match the extraction type. " +
                    "Expected: $types, actual: '$actualType'."
            )
    }

    private inner class ResultSetIterator : AbstractIterator<ResultRow>() {
        override fun computeNext() {
            if (resultSet.next())
                setNext(this@ResultRowsInstance)
            else
                done()
        }
    }
}
