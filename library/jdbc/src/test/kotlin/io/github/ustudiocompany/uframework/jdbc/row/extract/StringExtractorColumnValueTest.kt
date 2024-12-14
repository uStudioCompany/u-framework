package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.CHAR
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.ROW_ID_COLUMN_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.TEXT
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.VARCHAR
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getString
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class StringExtractorColumnValueTest : AbstractExtractorColumnValueTest() {

    init {

        "The `getString` method" - {
            container.executeSql(makeCreateTableSql())

            "when column index is valid" - {

                withData(
                    nameFn = { "when column type is '${it.first.dataType}'" },
                    testData
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    @Suppress("DestructuringDeclarationWithTooManyEntries")
                    withData(
                        nameFn = { "when column value is ${it.description} then the function should return this value" },
                        metadata.second
                    ) { (_, rowId, value, expected) ->
                        insertData(rowId, metadata.first.columnName, value)
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)
                        executeQuery(selectSql) {
                            val result = getString(metadata.first.columnIndex)
                            result shouldBeSuccess expected
                        }
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.dataType}' then the function should return an error" },
                    getColumnsExclude(TEXT, VARCHAR, CHAR)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    container.executeSql(makeInsertEmptyRowSql())
                    executeQuery(makeSelectEmptyRowSql()) {
                        val result = getString(metadata.columnIndex)
                        result.shouldBeFailure()
                        val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.TypeMismatch>()
                        cause.label shouldBe ColumnLabel.Index(metadata.columnIndex)
                    }
                }
            }

            "when column index is invalid then the function should return an error" {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                executeQuery(makeSelectEmptyRowSql()) {
                    val result = getString(INVALID_COLUMN_INDEX)
                    result.shouldBeFailure()
                    val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.UndefinedColumn>()
                    cause.label shouldBe ColumnLabel.Index(INVALID_COLUMN_INDEX)
                }
            }

            "when column name is valid" - {
                withData(
                    nameFn = { "when column type is '${it.first.dataType}'" },
                    testData
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    @Suppress("DestructuringDeclarationWithTooManyEntries")
                    withData(
                        nameFn = { "when column value is ${it.description} then the function should return this value" },
                        metadata.second
                    ) { (_, rowId, value, expected) ->
                        insertData(rowId, metadata.first.columnName, value)
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)
                        executeQuery(selectSql) {
                            getString(metadata.first.columnName).shouldBeSuccess(expected)
                        }
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.dataType}' then the function should return an error" },
                    getColumnsExclude(TEXT, VARCHAR, CHAR)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    container.executeSql(makeInsertEmptyRowSql())
                    executeQuery(makeSelectEmptyRowSql()) {
                        val result = getString(metadata.columnName)
                        result.shouldBeFailure()
                        val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.TypeMismatch>()
                        cause.label shouldBe ColumnLabel.Name(metadata.columnName)
                    }
                }
            }

            "when column name is invalid then the function should return an error" {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                executeQuery(makeSelectEmptyRowSql()) {
                    val result = getString(INVALID_COLUMN_NAME)
                    result.shouldBeFailure()
                    val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.UndefinedColumn>()
                    cause.label shouldBe ColumnLabel.Name(INVALID_COLUMN_NAME)
                }
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
        ),
    )

    private fun insertData(rowId: Int, columnName: String, value: String?) {
        val rowValue: String? = value?.let { "'$it'" }
        val sql = """
            | INSERT INTO $MULTI_COLUMN_TABLE_NAME($ROW_ID_COLUMN_NAME, $columnName)
            | VALUES ($rowId, $rowValue);
            """.trimMargin()
        container.executeSql(sql)
    }

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
    }
}
