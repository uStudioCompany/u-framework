package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeException
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.BOOLEAN_TYPE
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.MULTI_COLUMN_TABLE_NAME
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.getColumnsExclude
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.makeCreateTableSql
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.makeInsertEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.Companion.makeSelectEmptyRowSql
import io.github.ustudiocompany.uframework.jdbc.test.executeSql
import io.github.ustudiocompany.uframework.jdbc.test.postgresContainer
import io.github.ustudiocompany.uframework.jdbc.test.truncateTable
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.core.extensions.install
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

internal class BooleanTypeOrNullExtractorTest : AbstractExtractorTest() {

    init {

        "The extension function `getBooleanOrNull` of the `ResultRow` type" - {
            val dataSource = install(postgresContainer())
            val tm: TransactionManager = transactionManager(dataSource = dataSource)
            dataSource.executeSql(makeCreateTableSql())

            "when column index is valid" - {
                withData(
                    nameFn = { "when column type is '${it.displayType}'" },
                    columnTypes(EXPECTED_TYPE)
                ) { metadata ->

                    withData(
                        nameFn = { "when column value is '$it' then the function should return this value" },
                        listOf(
                            TRUE_VALUE,
                            FALSE_VALUE,
                            NULL_VALUE,
                        )
                    ) { value ->
                        dataSource.truncateTable(MULTI_COLUMN_TABLE_NAME)
                        dataSource.insertData(ROW_ID, metadata.columnName, value?.toString())

                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(ROW_ID)
                        val result = tm.executeQuery(selectSql) {
                            getBooleanOrNull(metadata.columnIndex)
                        }

                        result.shouldBeSuccess(value)
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.displayType}' then the function should return an exception" },
                    getColumnsExclude(EXPECTED_TYPE)
                ) { metadata ->
                    dataSource.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    dataSource.executeSql(makeInsertEmptyRowSql())

                    val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                        getBooleanOrNull(metadata.columnIndex)
                    }

                    val exceptionValue = result.shouldBeException()
                    exceptionValue.description.shouldBe(
                        "The column type with index '${metadata.columnIndex}' does not match the extraction type. " +
                            "Expected: [${EXPECTED_TYPE.dataType}], actual: '${metadata.dataType}'."
                    )
                }
            }

            "when column index is invalid then the function should return an exception" {
                dataSource.truncateTable(MULTI_COLUMN_TABLE_NAME)
                dataSource.executeSql(makeInsertEmptyRowSql())

                val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                    getBooleanOrNull(INVALID_COLUMN_INDEX)
                }

                val exceptionValue = result.shouldBeException()
                exceptionValue.description shouldBe "The column index '$INVALID_COLUMN_INDEX' is out of bounds."
            }
        }
    }

    companion object {
        private const val ROW_ID = 1
        private const val TRUE_VALUE = true
        private const val FALSE_VALUE = false
        private val NULL_VALUE: Boolean? = null
        private val EXPECTED_TYPE = BOOLEAN_TYPE
    }
}
