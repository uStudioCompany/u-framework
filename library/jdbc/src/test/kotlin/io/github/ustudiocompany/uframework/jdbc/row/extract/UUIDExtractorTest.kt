package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeJDBCError
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.UUID
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getUUID
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.util.*

internal class UUIDExtractorTest : AbstractExtractorTest() {

    init {

        "The extension function `getUUID` of the `ResultRow` type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(makeCreateTableSql())

            "when column index is valid" - {
                withData(
                    nameFn = { "when column type is '${it.displayType}'" },
                    columnTypes(EXPECTED_TYPE)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    withData(
                        nameFn = { "when column value is '${it.second}' then the function should return this value" },
                        listOf(
                            1 to VALID_VALUE,
                            2 to NULL_VALUE
                        )
                    ) { (rowId, value) ->
                        container.insertData(rowId, metadata.columnName, value?.toQuotes())
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)

                        val result = tm.executeQuery(selectSql) {
                            getUUID(metadata.columnIndex)
                        }

                        result shouldBeSuccess value
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.displayType}' then the function should return an error" },
                    getColumnsExclude(EXPECTED_TYPE)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)

                    container.executeSql(makeInsertEmptyRowSql())
                    val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                        getUUID(metadata.columnIndex)
                    }

                    val error = result.shouldBeJDBCError()
                    error.description.shouldBe(
                        "The column type with index '${metadata.columnIndex}' does not match the extraction type. " +
                            "Expected: [${EXPECTED_TYPE.dataType}], actual: '${metadata.dataType}'."
                    )
                }
            }

            "when column index is invalid then the function should return an error" {
                container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                container.executeSql(makeInsertEmptyRowSql())

                val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                    getUUID(INVALID_COLUMN_INDEX)
                }

                val error = result.shouldBeJDBCError()
                error.description shouldBe "The column index '$INVALID_COLUMN_INDEX' is out of bounds."
            }
        }
    }

    private fun UUID.toQuotes(): String = "'$this'"

    companion object {
        private val VALID_VALUE: UUID = java.util.UUID.randomUUID()
        private val NULL_VALUE: UUID? = null
        private val EXPECTED_TYPE = UUID
    }
}
