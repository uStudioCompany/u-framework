package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.row.extractor.DataExtractor
import java.sql.ResultSet
import java.sql.ResultSetMetaData

internal class ResultRowsInstance(
    private val resultSet: ResultSet,
) : ResultRows, ResultRow {

    override fun <ValueT> extract(
        index: Int,
        expectedColumnTypes: ResultRow.ColumnTypes,
        block: DataExtractor<ValueT>
    ): JDBCResult<ValueT> = try {
        val metadata = resultSet.metaData
        resultWith {
            metadata.checkColumnIndex(index = index).raise()
            metadata.checkExpectedColumnType(index = index, expectedColumnTypes).raise()
            block(index, resultSet)
        }
    } catch (expected: Exception) {
        JDBCError(
            description = "Error while extracting data from the result set",
            exception = expected
        ).asFailure()
    }

    override fun iterator(): Iterator<ResultRow> = ResultSetIterator()

    private fun ResultSetMetaData.checkColumnIndex(index: Int): ResultK<Unit, JDBCError> =
        if (index < 1 || index > columnCount)
            JDBCError(description = "The column index '$index' is out of bounds.").asFailure()
        else
            Success.asUnit

    private fun ResultSetMetaData.checkExpectedColumnType(
        index: Int,
        types: ResultRow.ColumnTypes
    ): ResultK<Unit, JDBCError> {
        val actualType = getColumnTypeName(index)
        return if (actualType in types)
            Success.asUnit
        else
            JDBCError(
                description = "The column type with index '$index' does not match the extraction type. " +
                    "Expected: $types, actual: '$actualType'."
            ).asFailure()
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
