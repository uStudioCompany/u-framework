package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.TIMESTAMP
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getTimestamp
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.sql.Timestamp

internal class TimestampExtractorTest : AbstractExtractorTest() {

    init {

        "The extension function `getTimestamp` of the `ResultRow` type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(makeCreateTableSql())

            "when column index is valid" - {
                withData(
                    nameFn = { "when column type is '${it.dataType}'" },
                    columnTypes(TIMESTAMP)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    withData(
                        nameFn = { "when column value is '${it.second}' then the function should return this value" },
                        listOf(
                            1 to MAX_VALUE,
                            2 to MIN_VALUE,
                            3 to NULL_VALUE
                        )
                    ) { (rowId, value) ->
                        container.insertData(rowId, metadata.columnName, value?.toQuotes())
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)

                        val result = tm.executeQuery(selectSql) {
                            getTimestamp(metadata.columnIndex)
                        }

                        result shouldBeSuccess value
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.dataType}' then the function should return an error" },
                    getColumnsExclude(TIMESTAMP)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    container.executeSql(makeInsertEmptyRowSql())

                    val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                        getTimestamp(metadata.columnIndex)
                    }

                    result.shouldBeFailure()
                    val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.TypeMismatch>()
                    cause.label shouldBe ColumnLabel.Index(metadata.columnIndex)
                }
            }

            "when column index is invalid then the function should return an error" {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                    getTimestamp(INVALID_COLUMN_INDEX)
                }

                result.shouldBeFailure()
                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.UndefinedColumn>()
                cause.label shouldBe ColumnLabel.Index(INVALID_COLUMN_INDEX)
            }
        }
    }

    private fun Timestamp.toQuotes(): String = "'$this'"

    companion object {
        private val MAX_VALUE = Timestamp.valueOf("2100-01-01 00:00:00")
        private val MIN_VALUE = Timestamp.valueOf("1900-01-01 00:00:00")
        private val NULL_VALUE: Timestamp? = null
    }
}
