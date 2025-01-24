package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.andThen
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.liftToTransactionException
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.ROW_ID_COLUMN_NAME
import io.github.ustudiocompany.uframework.jdbc.test.executeSql
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import javax.sql.DataSource

internal abstract class AbstractExtractorTest : IntegrationTest() {

    protected fun <ValueT> TransactionManager.executeQuery(
        sql: String,
        block: ResultRow.() -> JDBCResult<ValueT>
    ): TransactionResult<ValueT, Nothing> =
        useTransaction { connection ->
            connection.preparedStatement(sql)
                .andThen { statement ->
                    statement.query()
                        .andThen { rows ->
                            val row = rows.first()
                            block(row)
                        }
                }
                .liftToTransactionException()
        }

    protected fun DataSource.insertData(rowId: Int, columnName: String, value: String?) {
        val sql = """
            | INSERT INTO $MULTI_COLUMN_TABLE_NAME($ROW_ID_COLUMN_NAME, $columnName)
            | VALUES ($rowId, $value);
        """.trimMargin()
        executeSql(sql)
    }

    companion object {

        @JvmStatic
        val INVALID_COLUMN_INDEX = 0

        @JvmStatic
        val INVALID_COLUMN_NAME = "invalid_column"
    }
}
