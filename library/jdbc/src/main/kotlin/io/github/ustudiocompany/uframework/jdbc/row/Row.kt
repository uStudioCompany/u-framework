package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.functional.Result
import io.github.airflux.functional.error
import io.github.airflux.functional.success
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.exception.isConnectionError
import io.github.ustudiocompany.uframework.jdbc.exception.isUndefinedColumn
import java.sql.ResultSet
import java.sql.SQLException

@JvmInline
public value class Row(private val resultSet: ResultSet) {

    public abstract class ColumnValueExtractor<T> {

        public abstract fun extract(row: Row, index: Int): Result<T?, JDBCErrors>
        public abstract fun extract(row: Row, columnName: String): Result<T?, JDBCErrors>

        protected fun resultSet(row: Row): ResultSet = row.resultSet

        protected fun findColumnIndex(row: Row, name: String): Result<Int, JDBCErrors> = try {
            row.resultSet.findColumn(name).success()
        } catch (expected: SQLException) {
            val error = when {
                expected.isConnectionError -> JDBCErrors.Connection(expected)
                expected.isUndefinedColumn -> JDBCErrors.Rows.UndefinedColumn(name, expected)
                else -> JDBCErrors.UnexpectedError(expected)
            }
            error.error()
        }
    }
}
