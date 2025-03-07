package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.ResultK
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
    ): JDBCResult<ValueT> =
        ResultK.catchWith(
            catch = { exception ->
                JDBCError(description = "Error while extracting data from the result set", exception = exception)
            }
        ) {
            resultWith {
                val metadata = resultSet.metaData
                metadata.checkColumnIndex(index = index)
                metadata.checkExpectedColumnType(index = index, expectedColumnTypes)
                block(index, resultSet)
            }
        }

    override fun iterator(): Iterator<ResultRow> = ResultSetIterator()

    context(ResultK.Raise<JDBCError>)
    private fun ResultSetMetaData.checkColumnIndex(index: Int) {
        if (index < 1 || index > columnCount)
            raise(JDBCError(description = "The column index '$index' is out of bounds."))
    }

    context(ResultK.Raise<JDBCError>)
    private fun ResultSetMetaData.checkExpectedColumnType(
        index: Int,
        types: ResultRow.ColumnTypes
    ) {
        val actualType = getColumnTypeName(index)
        if (actualType !in types)
            raise(
                JDBCError(
                    description = "The column type with index '$index' does not match the extraction type. " +
                        "Expected: $types, actual: '$actualType'."
                )
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
