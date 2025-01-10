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
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.JSONB
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getJSONB
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class JsonbExtractorTest : AbstractExtractorTest() {

    init {

        "The extension function `getJsonb` of the `ResultRow` type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(makeCreateTableSql())

            "when column index is valid" - {
                withData(
                    ts = columnTypes(JSONB),
                    nameFn = { "when column type is '${it.dataType}'" },
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    withData(
                        nameFn = { "when column value is '${it.second}' then function should return it" },
                        ts = listOf(
                            1 to JSON_NULL_VALUE,
                            2 to JSON_VALUE,
                        ),
                    ) { (rowId, value) ->
                        container.insertData(rowId, metadata.columnName, value?.toQuotes())
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)

                        val result = tm.executeQuery(selectSql) {
                            getJSONB(metadata.columnIndex)
                        }

                        result shouldBeSuccess value
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.dataType}' then function should return an error}" },
                    ts = getColumnsExclude(JSONB),
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    container.executeSql(makeInsertEmptyRowSql())

                    val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                        getJSONB(metadata.columnIndex)
                    }

                    result.shouldBeFailure()
                    val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.TypeMismatch>()
                    cause.label shouldBe ColumnLabel.Index(metadata.columnIndex)
                }
            }

            "when column index is invalid then function should return an error" {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                    getJSONB(INVALID_COLUMN_INDEX)
                }

                result.shouldBeFailure()
                val cause = result.cause.shouldBeInstanceOf<JDBCErrors.Row.UndefinedColumn>()
                cause.label shouldBe ColumnLabel.Index(INVALID_COLUMN_INDEX)
            }
        }
    }

    private fun String.toQuotes(): String = "'$this'"

    companion object {
        private const val JSON_VALUE = """{"number": 1, "string": "string value", "boolean": true}"""
        private val JSON_NULL_VALUE = null
    }
}
