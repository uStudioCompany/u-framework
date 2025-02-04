package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.MaybeFailure
import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.jdbcFail
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
        jdbcFail(
            description = "Error while extracting data from the result set",
            exception = expected
        )
    }

    override fun iterator(): Iterator<ResultRow> = ResultSetIterator()

    private fun ResultSetMetaData.checkColumnIndex(index: Int): MaybeFailure<JDBCError> =
        if (index < 1 || index > columnCount)
            jdbcFail(description = "The column index '$index' is out of bounds.")
        else
            Success.asUnit

    private fun ResultSetMetaData.checkExpectedColumnType(
        index: Int,
        types: ResultRow.ColumnTypes
    ): MaybeFailure<JDBCError> {
        val actualType = getColumnTypeName(index)
        return if (actualType in types)
            Success.asUnit
        else
            jdbcFail(
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
