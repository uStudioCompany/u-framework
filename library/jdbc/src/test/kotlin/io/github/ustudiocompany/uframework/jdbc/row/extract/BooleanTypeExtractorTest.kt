package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.error.TransactionError
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.BOOLEAN
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getBoolean
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class BooleanTypeExtractorTest : AbstractExtractorTest() {

    init {

        "The extension function `getBoolean` of the `ResultRow` type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(makeCreateTableSql())

            "when column index is valid" - {
                withData(
                    nameFn = { "when column type is '${it.dataType}'" },
                    columnTypes(BOOLEAN)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    withData(
                        nameFn = { "when column value is '${it.second}' then the function should return this value" },
                        listOf(
                            1 to TRUE_VALUE,
                            2 to FALSE_VALUE,
                            3 to NULL_VALUE,
                        )
                    ) { (rowId, value) ->
                        container.insertData(rowId, metadata.columnName, value?.toString())

                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)
                        val result = tm.executeQuery(selectSql) {
                            getBoolean(metadata.columnIndex)
                        }

                        result.shouldBeSuccess(value)
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.dataType}' then the function should return an error" },
                    getColumnsExclude(BOOLEAN)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    container.executeSql(makeInsertEmptyRowSql())

                    val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                        getBoolean(metadata.columnIndex)
                    }

                    result.shouldBeFailure()
                    val cause = result.cause.shouldBeInstanceOf<TransactionError.Row.TypeMismatch>()
                    cause.label shouldBe ColumnLabel.Index(metadata.columnIndex)
                }
            }

            "when column index is invalid then the function should return an error" {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                    getBoolean(INVALID_COLUMN_INDEX)
                }

                result.shouldBeFailure()
                val cause = result.cause.shouldBeInstanceOf<TransactionError.Row.UndefinedColumn>()
                cause.label shouldBe ColumnLabel.Index(INVALID_COLUMN_INDEX)
            }
        }
    }

    companion object {
        private const val TRUE_VALUE = true
        private const val FALSE_VALUE = false
        private val NULL_VALUE: Boolean? = null
    }
}
