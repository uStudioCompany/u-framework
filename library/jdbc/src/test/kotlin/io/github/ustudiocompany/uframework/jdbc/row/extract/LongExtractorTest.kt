package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.BIGINT
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getLong
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class LongExtractorTest : AbstractExtractorTest() {

    init {

        "The extension function `getLong` of the `ResultRow` type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(makeCreateTableSql())

            "when column index is valid" - {
                withData(
                    nameFn = { "when column type is '${it.dataType}'" },
                    columnTypes(BIGINT)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    withData(
                        nameFn = { "when column value is '${it.second}' then the function should return this value" },
                        listOf(
                            1 to MAX_VALUE,
                            2 to ZERO_VALUE,
                            3 to MIN_VALUE,
                            4 to NULL_VALUE
                        )
                    ) { (rowId, value) ->
                        container.insertData(rowId, metadata.columnName, value?.toString())
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)

                        val result = tm.executeQuery(selectSql) {
                            getLong(metadata.columnIndex)
                        }

                        result shouldBeSuccess value
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.dataType}' then the function should return an error" },
                    getColumnsExclude(BIGINT)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    container.executeSql(makeInsertEmptyRowSql())

                    val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                        getLong(metadata.columnIndex)
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
                    getLong(INVALID_COLUMN_INDEX)
                }

                result.shouldBeFailure()
                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.UndefinedColumn>()
                cause.label shouldBe ColumnLabel.Index(INVALID_COLUMN_INDEX)
            }
        }
    }

    companion object {
        private const val MAX_VALUE = Long.MAX_VALUE
        private const val MIN_VALUE = Long.MIN_VALUE
        private const val ZERO_VALUE = 0L
        private val NULL_VALUE: Long? = null
    }
}
