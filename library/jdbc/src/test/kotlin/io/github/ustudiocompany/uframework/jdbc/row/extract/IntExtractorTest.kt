package io.github.ustudiocompany.uframework.jdbc.row.extract

import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extract.MultiColumnTable.INTEGER
import io.github.ustudiocompany.uframework.jdbc.row.extractor.getInt
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class IntExtractorTest : AbstractExtractorTest() {

    init {

        "The extension function `getInt` of the `ResultRow` type" - {
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
                            1 to MAX_VALUE,
                            2 to ZERO_VALUE,
                            3 to MIN_VALUE,
                            4 to NULL_VALUE
                        )
                    ) { (rowId, value) ->
                        container.insertData(rowId, metadata.columnName, value?.toString())
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(rowId)
                        val result = tm.executeQuery(selectSql) {
                            getInt(metadata.columnIndex)
                        }

                        result.shouldBeSuccess(value)
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.displayType}' then the function should return an error" },
                    getColumnsExclude(EXPECTED_TYPE)
                ) { metadata ->
                    container.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    container.executeSql(makeInsertEmptyRowSql())

                    val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                        getInt(metadata.columnIndex)
                    }

                    result.shouldBeFailure()
                    val error = result.cause.shouldBeInstanceOf<JDBCError>()
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
                    getInt(INVALID_COLUMN_INDEX)
                }

                result.shouldBeFailure()
                val error = result.cause.shouldBeInstanceOf<JDBCError>()
                error.description shouldBe "The column index '$INVALID_COLUMN_INDEX' is out of bounds."
            }
        }
    }

    companion object {
        private const val MAX_VALUE = Int.MAX_VALUE
        private const val MIN_VALUE = Int.MIN_VALUE
        private const val ZERO_VALUE = 0
        private val NULL_VALUE: Int? = null
        private val EXPECTED_TYPE = INTEGER
    }
}
