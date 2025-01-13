package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.ROW_ID_COLUMN_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.JSONB
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getJsonb
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class JsonbExtractorColumnValueTest : AbstractExtractorColumnValueTest() {

    init {

        "The `getJsonb` function" - {
            container.executeSql(makeCreateTableSql())

            "when column index is valid" - {
                withData(
                    ts = columnTypes(JSONB),
                    nameFn = { "when column type is '${it.displayType}'" },
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    withData(
                        nameFn = { "when column value is '${it.second}' then function should return it" },
                        ts = listOf(
                            1 to JSON_NULL_VALUE,
                            2 to JSON_VALUE,
                        ),
                    ) { (rowId, value) ->
                        insertData(rowId, metadata.columnName, value)
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)
                        executeQuery(selectSql) {
                            val result = getJsonb(metadata.columnIndex)
                            result shouldBeSuccess value
                        }
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.displayType}' then function should return an error}" },
                    ts = getColumnsExclude(JSONB),
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    container.executeSql(makeInsertEmptyRowSql())

                    executeQuery(makeSelectEmptyRowSql()) {
                        val result = getJsonb(metadata.columnIndex)
                        result.shouldBeFailure()
                        val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.TypeMismatch>()
                        cause.label shouldBe ColumnLabel.Index(metadata.columnIndex)
                    }
                }
            }

            "when column index is invalid then function should return an error" {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                executeQuery(makeSelectEmptyRowSql()) {
                    val result = getJsonb(INVALID_COLUMN_INDEX)
                    result.shouldBeFailure()
                    val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.UndefinedColumn>()
                    cause.label shouldBe ColumnLabel.Index(INVALID_COLUMN_INDEX)
                }
            }

            "when column name is valid" - {
                withData(
                    nameFn = { "when column type is '${it.displayType}'" },
                    ts = columnTypes(JSONB),
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    withData(
                        nameFn = { "when column value is '${it.second}' then function should return it" },
                        ts = listOf(
                            1 to JSON_VALUE,
                            2 to JSON_NULL_VALUE,
                        ),
                    ) { (rowId, value) ->
                        insertData(rowId, metadata.columnName, value)
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)
                        executeQuery(selectSql) {
                            getJsonb(metadata.columnName).shouldBeSuccess(value)
                        }
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.displayType}' then function should return error" },
                    ts = getColumnsExclude(JSONB),
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    container.executeSql(makeInsertEmptyRowSql())

                    executeQuery(makeSelectEmptyRowSql()) {
                        val result = getJsonb(metadata.columnName)
                        result.shouldBeFailure()
                        val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.TypeMismatch>()
                        cause.label shouldBe ColumnLabel.Name(metadata.columnName)
                    }
                }
            }

            "when column name is invalid then function should return an error" {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                executeQuery(makeSelectEmptyRowSql()) {
                    val result = getJsonb(INVALID_COLUMN_NAME)
                    result.shouldBeFailure()
                    val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.UndefinedColumn>()
                    cause.label shouldBe ColumnLabel.Name(INVALID_COLUMN_NAME)
                }
            }
        }
    }

    private fun insertData(rowId: Int, columnName: String, value: String?) {
        val rowValue: String? = value?.let { "'$it'" }
        val sql = """
            | INSERT INTO $MULTI_COLUMN_TABLE_NAME($ROW_ID_COLUMN_NAME, $columnName)
            | VALUES ($rowId, $rowValue);
        """.trimMargin()
        container.executeSql(sql)
    }

    companion object {
        private const val JSON_VALUE = """{"number": 1, "string": "string value", "boolean": true}"""
        private val JSON_NULL_VALUE = null
    }
}
