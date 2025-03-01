package io.github.ustudiocompany.uframework.jdbc.row.extractor

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldContainExceptionInstance
import io.github.ustudiocompany.uframework.jdbc.row.extractor.MultiColumnTable.BIG_SERIAL_TYPE
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

@OptIn(AirfluxTypesExperimental::class)
internal class BigSerialTypeExtractorTest : AbstractExtractorTest() {

    init {

        "The extension function `getBigSerial` of the `ResultRow` type" - {
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
                            MAX_VALUE,
                            ZERO_VALUE,
                            MIN_VALUE
                        )
                    ) { value ->
                        dataSource.truncateTable(MULTI_COLUMN_TABLE_NAME)
                        dataSource.insertData(ROW_ID, metadata.columnName, value.toString())
                        val selectSql = MultiColumnTable.makeSelectAllColumnsSql(ROW_ID)

                        val result = tm.executeQuery(selectSql) {
                            getBigSerial(metadata.columnIndex)
                        }

                        result shouldBeSuccess value
                    }
                }

                withData(
                    nameFn = { "when column type is '${it.displayType}' then the function should return an exception" },
                    getColumnsExclude(EXPECTED_TYPE)
                ) { metadata ->
                    dataSource.truncateTable(MULTI_COLUMN_TABLE_NAME)
                    dataSource.executeSql(makeInsertEmptyRowSql())

                    val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                        getBigSerial(metadata.columnIndex)
                    }

                    val exception = result.shouldContainExceptionInstance()
                    exception.description.shouldBe(
                        "The column type with index '${metadata.columnIndex}' does not match the extraction type. " +
                            "Expected: [${EXPECTED_TYPE.dataType}], actual: '${metadata.dataType}'."
                    )
                }
            }

            "when column index is invalid then the function should return an exception" {
                dataSource.truncateTable(MULTI_COLUMN_TABLE_NAME)
                dataSource.executeSql(makeInsertEmptyRowSql())

                val result = tm.executeQuery(makeSelectEmptyRowSql()) {
                    getBigSerial(INVALID_COLUMN_INDEX)
                }

                val exception = result.shouldContainExceptionInstance()
                exception.description shouldBe "The column index '$INVALID_COLUMN_INDEX' is out of bounds."
            }
        }
    }

    companion object {
        private const val ROW_ID = 1
        private const val MAX_VALUE = Long.MAX_VALUE
        private const val MIN_VALUE = Long.MIN_VALUE
        private const val ZERO_VALUE = 0L
        private val EXPECTED_TYPE = BIG_SERIAL_TYPE
    }
}
