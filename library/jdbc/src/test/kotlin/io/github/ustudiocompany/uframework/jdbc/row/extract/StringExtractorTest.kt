package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeIncident
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.CHAR
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.TEXT
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.VARCHAR
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getString
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class StringExtractorTest : AbstractExtractorTest() {

    init {

        "The extension function `getString` of the `ResultRow` type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(makeCreateTableSql())

            "when column index is valid" - {

                withData(
                    nameFn = { "when column type is '${it.first.displayType}'" },
                    testData
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    @Suppress("DestructuringDeclarationWithTooManyEntries")
                    withData(
                        nameFn = {
                            "when column value is ${it.description} then the function should return this value"
                        },
                        metadata.second
                    ) { (_, rowId, value, expected) ->
                        container.insertData(rowId, metadata.first.columnName, value?.toQuotes())
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)

                        val result = tm.executeQuery(selectSql) {
                            getString(metadata.first.columnIndex)
                        }

                        result shouldBeSuccess expected
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.displayType}' then the function should return an incident" },
                    getColumnsExclude(EXPECTED_TYPES)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    container.executeSql(makeInsertEmptyRowSql())

                    val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                        getString(metadata.columnIndex)
                    }

                    val error = result.shouldBeIncident()
                    error.description.shouldBe(
                        "The column type with index '${metadata.columnIndex}' does not match the extraction type. " +
                            "Expected: ${EXPECTED_TYPES.map { it.dataType }}, actual: '${metadata.dataType}'."
                    )
                }
            }

            "when column index is invalid then the function should return an incident" {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                    getString(INVALID_COLUMN_INDEX)
                }

                val error = result.shouldBeIncident()
                error.description shouldBe "The column index '$INVALID_COLUMN_INDEX' is out of bounds."
            }
        }
    }

    private val testData = listOf(
        TEXT to listOf(
            TestDataItem(EMPTY_STRING_DESCRIPTION, 1, EMPTY_STRING, EMPTY_STRING),
            TestDataItem(BLANK_STRING_DESCRIPTION, 2, BLANK_STRING, BLANK_STRING),
            TestDataItem(FILL_STRING_DESCRIPTION, 3, FILL_STRING, FILL_STRING),
            TestDataItem(NULL_VALUE_STRING_DESCRIPTION, 4, NULL_VALUE_STRING, NULL_VALUE_STRING)
        ),
        VARCHAR to listOf(
            TestDataItem(EMPTY_STRING_DESCRIPTION, 1, EMPTY_STRING, EMPTY_STRING),
            TestDataItem(BLANK_STRING_DESCRIPTION, 2, BLANK_STRING, BLANK_STRING),
            TestDataItem(FILL_STRING_DESCRIPTION, 3, FILL_STRING, FILL_STRING),
            TestDataItem(NULL_VALUE_STRING_DESCRIPTION, 4, NULL_VALUE_STRING, NULL_VALUE_STRING)
        ),
        CHAR to listOf(
            TestDataItem(EMPTY_STRING_DESCRIPTION, 1, EMPTY_STRING, "    "),
            TestDataItem(BLANK_STRING_DESCRIPTION, 2, BLANK_STRING, "    "),
            TestDataItem(FILL_STRING_DESCRIPTION, 3, FILL_STRING, FILL_STRING),
            TestDataItem(NULL_VALUE_STRING_DESCRIPTION, 4, NULL_VALUE_STRING, NULL_VALUE_STRING)
        )
    )

    private fun String.toQuotes(): String = "'$this'"

    private data class TestDataItem(
        val description: String,
        val rowID: Int,
        val insertValue: String?,
        val expectedValue: String?
    )

    companion object {
        private const val EMPTY_STRING = ""
        private const val BLANK_STRING = " "
        private const val FILL_STRING = "test"
        private val NULL_VALUE_STRING: String? = null

        private const val EMPTY_STRING_DESCRIPTION = "empty"
        private const val BLANK_STRING_DESCRIPTION = "blank"
        private const val FILL_STRING_DESCRIPTION = "'$FILL_STRING'"
        private const val NULL_VALUE_STRING_DESCRIPTION: String = "null"

        private val EXPECTED_TYPES = listOf(TEXT, VARCHAR, CHAR)
    }
}
