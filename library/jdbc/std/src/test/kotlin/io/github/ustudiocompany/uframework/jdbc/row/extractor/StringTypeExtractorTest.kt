package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeException
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.CHAR_TYPE
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.TEXT_TYPE
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.VARCHAR_TYPE
import io.github.ustudiocompany.uframework.jdbc.test.executeSql
import io.github.ustudiocompany.uframework.jdbc.test.postgresContainer
import io.github.ustudiocompany.uframework.jdbc.test.truncateTable
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.core.extensions.install
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class StringTypeExtractorTest : AbstractExtractorTest() {

    init {

        "The extension function `getString` of the `ResultRow` type" - {
            val dataSource = install(postgresContainer())
            val tm: TransactionManager = transactionManager(dataSource = dataSource)
            dataSource.executeSql(makeCreateTableSql())

            "when column index is valid" - {

                withData(
                    nameFn = { "when column type is '${it.displayType}'" },
                    columnTypes(TEXT_TYPE, VARCHAR_TYPE, CHAR_TYPE)
                ) { metadata ->

                    withData(
                        nameFn = {
                            "when column value is ${it.description} then the function should return this value"
                        },
                        nonNullTestData[metadata]!!
                    ) { (_, value, expected) ->
                        dataSource.truncateTable(MULTI_COLUMN_TABLE_NAME)
                        dataSource.insertData(ROW_ID, metadata.columnName, value?.toQuotes())
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(ROW_ID)

                        val result = tm.executeQuery(selectSql) {
                            getString(metadata.columnIndex)
                        }

                        result shouldBeSuccess expected
                    }

                    withData(
                        nameFn = {
                            "when column value is ${it.description} then the function should return an exception"
                        },
                        nullableTestData[metadata]!!
                    ) { (_, value, expected) ->
                        dataSource.truncateTable(MULTI_COLUMN_TABLE_NAME)
                        dataSource.insertData(ROW_ID, metadata.columnName, value?.toQuotes())
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(ROW_ID)

                        val result = tm.executeQuery(selectSql) {
                            getString(metadata.columnIndex)
                        }

                        val exceptionValue = result.shouldBeException()
                        exceptionValue.description.shouldBe("The value of the column with index '${metadata.columnIndex}' is null.")
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.displayType}' then the function should return an exception" },
                    getColumnsExclude(EXPECTED_TYPES)
                ) { metadata ->
                    dataSource.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    dataSource.executeSql(makeInsertEmptyRowSql())

                    val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                        getString(metadata.columnIndex)
                    }

                    val exceptionValue = result.shouldBeException()
                    exceptionValue.description.shouldBe(
                        "The column type with index '${metadata.columnIndex}' does not match the extraction type. " +
                            "Expected: ${EXPECTED_TYPES.map { it.dataType }}, actual: '${metadata.dataType}'."
                    )
                }
            }

            "when column index is invalid then the function should return an exception" {
                dataSource.truncateTable(MULTI_COLUMN_TABLE_NAME)
                dataSource.executeSql(makeInsertEmptyRowSql())

                val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                    getString(INVALID_COLUMN_INDEX)
                }

                val exceptionValue = result.shouldBeException()
                exceptionValue.description shouldBe "The column index '$INVALID_COLUMN_INDEX' is out of bounds."
            }
        }
    }

    private val nonNullTestData = mapOf(
        MultiColumnTable.TEXT_TYPE to listOf(
            TestDataItem(EMPTY_STRING_DESCRIPTION, EMPTY_STRING, EMPTY_STRING),
            TestDataItem(BLANK_STRING_DESCRIPTION, BLANK_STRING, BLANK_STRING),
            TestDataItem(FILL_STRING_DESCRIPTION, FILL_STRING, FILL_STRING)
        ),
        MultiColumnTable.VARCHAR_TYPE to listOf(
            TestDataItem(EMPTY_STRING_DESCRIPTION, EMPTY_STRING, EMPTY_STRING),
            TestDataItem(BLANK_STRING_DESCRIPTION, BLANK_STRING, BLANK_STRING),
            TestDataItem(FILL_STRING_DESCRIPTION, FILL_STRING, FILL_STRING)
        ),
        MultiColumnTable.CHAR_TYPE to listOf(
            TestDataItem(EMPTY_STRING_DESCRIPTION, EMPTY_STRING, "    "),
            TestDataItem(BLANK_STRING_DESCRIPTION, BLANK_STRING, "    "),
            TestDataItem(FILL_STRING_DESCRIPTION, FILL_STRING, FILL_STRING)
        )
    )

    private val nullableTestData = mapOf(
        MultiColumnTable.TEXT_TYPE to listOf(
            TestDataItem(NULL_VALUE_STRING_DESCRIPTION, NULL_VALUE_STRING, NULL_VALUE_STRING)
        ),
        MultiColumnTable.VARCHAR_TYPE to listOf(
            TestDataItem(NULL_VALUE_STRING_DESCRIPTION, NULL_VALUE_STRING, NULL_VALUE_STRING)
        ),
        MultiColumnTable.CHAR_TYPE to listOf(
            TestDataItem(NULL_VALUE_STRING_DESCRIPTION, NULL_VALUE_STRING, NULL_VALUE_STRING)
        )
    )

    private fun String.toQuotes(): String = "'$this'"

    private data class TestDataItem(
        val description: String,
        val insertValue: String?,
        val expectedValue: String?
    )

    companion object {
        private const val ROW_ID = 1
        private const val EMPTY_STRING = ""
        private const val BLANK_STRING = " "
        private const val FILL_STRING = "test"
        private val NULL_VALUE_STRING: String? = null

        private const val EMPTY_STRING_DESCRIPTION = "empty"
        private const val BLANK_STRING_DESCRIPTION = "blank"
        private const val FILL_STRING_DESCRIPTION = "'$FILL_STRING'"
        private const val NULL_VALUE_STRING_DESCRIPTION: String = "null"

        private val EXPECTED_TYPES = listOf(TEXT_TYPE, VARCHAR_TYPE, CHAR_TYPE)
    }
}
