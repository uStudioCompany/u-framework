package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.Success
import io.github.airflux.commons.types.resultk.asFailure
import io.github.airflux.commons.types.resultk.getOrForward
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.TransactionError
import io.github.ustudiocompany.uframework.jdbc.generalExceptionHandling
import io.github.ustudiocompany.uframework.jdbc.row.extractor.DataExtractorWith
import java.sql.ResultSet
import java.sql.ResultSetMetaData

internal class ResultRowsInstance(
    private val resultSet: ResultSet,
) : ResultRows, ResultRow {

    override fun <T> extractWith(index: Int, types: ResultRow.Types, block: DataExtractorWith<T>): JDBCResult<T?> =
        generalExceptionHandling {
            try {
                val metadata = resultSet.metaData
                metadata.checkIndex(index = index).getOrForward { return it }
                metadata.checkType(index = index, types).getOrForward { return it }
                block(index, resultSet)
            } catch (expected: ClassCastException) {
                TransactionError.Row.ReadColumn(index, expected).asFailure()
            }
        }

    override fun iterator(): Iterator<ResultRow> = ResultSetIterator()

    private fun ResultSetMetaData.checkIndex(index: Int): JDBCResult<Unit> =
        if (index < 1 || index > columnCount)
            TransactionError.Row.UndefinedColumn(index).asFailure()
        else
            Success.asUnit

    private fun ResultSetMetaData.checkType(index: Int, types: ResultRow.Types): JDBCResult<Unit> {
        val actualType = getColumnTypeName(index)
        return if (actualType in types)
            Success.asUnit
        else
            TransactionError.Row.TypeMismatch(index, types.toString(), actualType).asFailure()
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
