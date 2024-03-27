package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.airflux.functional.kotest.shouldBeError
import io.github.airflux.functional.kotest.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.ROW_ID_COLUMN_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.TIMESTAMP
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getTimestamp
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.sql.Timestamp

internal class TimestampExtractorColumnValueTest : AbstractExtractorColumnValueTest() {

    init {

        "The `getTimestamp` method" - {
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
                        insertData(rowId, metadata.columnName, value)
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)
                        executeQuery(selectSql) {
                            getTimestamp(metadata.columnIndex).shouldBeSuccess(value)
                        }
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.dataType}' then the function should return an error" },
                    getColumnsExclude(TIMESTAMP)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    container.executeSql(makeInsertEmptyRowSql())
                    executeQuery(makeSelectEmptyRowSql()) {
                        val failure = getTimestamp(metadata.columnIndex).shouldBeError()
                        val cause = failure.cause.shouldBeInstanceOf<JDBCErrors.Row.TypeMismatch>()
                        cause.label shouldBe ColumnLabel.Index(metadata.columnIndex)
                    }
                }
            }

            "when column index is invalid then the function should return an error" - {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                executeQuery(makeSelectEmptyRowSql()) {
                    val failure = getTimestamp(INVALID_COLUMN_INDEX).shouldBeError()
                    val cause = failure.cause.shouldBeInstanceOf<JDBCErrors.Row.UndefinedColumn>()
                    cause.label shouldBe ColumnLabel.Index(INVALID_COLUMN_INDEX)
                }
            }

            "when column name is valid" - {
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
                        insertData(rowId, metadata.columnName, value)
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)
                        executeQuery(selectSql) {
                            getTimestamp(metadata.columnName).shouldBeSuccess(value)
                        }
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.dataType}' then the function should return an error" },
                    getColumnsExclude(TIMESTAMP)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    container.executeSql(makeInsertEmptyRowSql())
                    executeQuery(makeSelectEmptyRowSql()) {
                        val failure = getTimestamp(metadata.columnName).shouldBeError()
                        val cause = failure.cause.shouldBeInstanceOf<JDBCErrors.Row.TypeMismatch>()
                        cause.label shouldBe ColumnLabel.Name(metadata.columnName)
                    }
                }
            }

            "when column name is invalid then the function should return an error" - {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                executeQuery(makeSelectEmptyRowSql()) {
                    val failure = getTimestamp(INVALID_COLUMN_NAME).shouldBeError()
                    val cause = failure.cause.shouldBeInstanceOf<JDBCErrors.Row.UndefinedColumn>()
                    cause.label shouldBe ColumnLabel.Name(INVALID_COLUMN_NAME)
                }
            }
        }
    }

    private fun insertData(rowId: Int, columnName: String, value: Timestamp?) {
        val rowValue: String? = value?.let { "'$it'" }
        val sql = """
            | INSERT INTO $MULTI_COLUMN_TABLE_NAME($ROW_ID_COLUMN_NAME, $columnName)
            | VALUES ($rowId, $rowValue);
            """.trimMargin()
        container.executeSql(sql)
    }

    companion object {
        private val MAX_VALUE = Timestamp.valueOf("2100-01-01 00:00:00")
        private val MIN_VALUE = Timestamp.valueOf("1900-01-01 00:00:00")
        private val NULL_VALUE: Timestamp? = null
    }
}
