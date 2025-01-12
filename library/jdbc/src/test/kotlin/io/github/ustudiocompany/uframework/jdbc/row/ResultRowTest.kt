package io.github.ustudiocompany.uframework.jdbc.row

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.error.TransactionError
import io.github.ustudiocompany.uframework.jdbc.row.extractor.DataExtractor
import io.github.ustudiocompany.uframework.jdbc.sql.ColumnLabel
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.intellij.lang.annotations.Language

internal class ResultRowTest : IntegrationTest() {

    init {

        "The ResultRows type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(CREATE_TABLE)

            "when the table does not contain data" - {
                container.truncateTable(TABLE_NAME)
                val result = tm.executeQuery(ID_COLUMN_INDEX, TEXT_TYPE) { col, rs -> rs.getString(col) }

                "then the result should be empty" {
                    result.shouldBeSuccess()
                    result.value.shouldBeEmpty()
                }
            }

            "when the table contains data" - {

                "when the extraction is successful" - {

                    "when the extraction type is not equal to the column type" - {
                        container.truncateTable(TABLE_NAME)
                        container.executeSql(INSERT_SQL)
                        val result =
                            tm.executeQuery(STATUS_COLUMN_INDEX, TEXT_TYPE) { col, rs -> rs.getString(col) }

                        "then the result should contain all data from the database" {
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<TransactionError.Row.TypeMismatch>()
                        }
                    }

                    "when the extraction type is equal to the column type" - {
                        container.truncateTable(TABLE_NAME)
                        container.executeSql(INSERT_SQL)
                        val result = tm.executeQuery(ID_COLUMN_INDEX, TEXT_TYPE) { col, rs -> rs.getString(col) }

                        "then the result should contain all data from the database" {
                            result.shouldBeSuccess()
                            result.value shouldContainExactly listOf(ID_FIRST_ROW_VALUE, ID_SECOND_ROW_VALUE)
                        }
                    }
                }

                "when the extraction is failed" - {

                    "when column index is out of range" - {

                        "when the index less than min" - {
                            container.truncateTable(TABLE_NAME)
                            container.executeSql(INSERT_SQL)
                            val invalidIndex = 0
                            val result =
                                tm.executeQuery(invalidIndex, TEXT_TYPE) { col, rs -> rs.getString(col) }

                            "then the result should contain all data from the database" {
                                result.shouldBeFailure()
                                val error = result.cause.shouldBeInstanceOf<TransactionError.Row.UndefinedColumn>()
                                error.label shouldBe ColumnLabel.Index(invalidIndex)
                            }
                        }

                        "when the index greater than max" - {
                            container.truncateTable(TABLE_NAME)
                            container.executeSql(INSERT_SQL)
                            val invalidIndex = 3
                            val result =
                                tm.executeQuery(invalidIndex, TEXT_TYPE) { col, rs -> rs.getString(col) }

                            "then the result should contain all data from the database" {
                                result.shouldBeFailure()
                                val error = result.cause.shouldBeInstanceOf<TransactionError.Row.UndefinedColumn>()
                                error.label shouldBe ColumnLabel.Index(invalidIndex)
                            }
                        }
                    }

                    "when thrown some exception" - {
                        container.truncateTable(TABLE_NAME)
                        container.executeSql(INSERT_SQL)
                        val result = tm.executeQuery(ID_COLUMN_INDEX, TEXT_TYPE) { col, rs ->
                            error("Error")
                        }

                        "then the result should return the error" {
                            result.shouldBeFailure()
                            result.cause.shouldBeInstanceOf<TransactionError.Unexpected>()
                        }
                    }
                }
            }
        }
    }

    private fun TransactionManager.executeQuery(column: Int, types: ResultRow.Types, block: DataExtractor<String>) =
        useTransaction { connection ->
            connection.preparedStatement(SELECT_SQL)
                .andThen { statement ->
                    statement.query()
                        .andThen { rows ->
                            rows.traverse { row -> row.extract(column, types, block) }
                        }
                }
        }

    private companion object {
        private const val TABLE_NAME = "test_table"

        private const val ID_COLUMN_NAME = "id"
        private const val STATUS_COLUMN_NAME = "status"

        private const val ID_COLUMN_INDEX = 1
        private const val STATUS_COLUMN_INDEX = 2

        private const val ID_FIRST_ROW_VALUE = "f-r-id"
        private const val ID_SECOND_ROW_VALUE = "s-r-id"

        private const val STATUS_FIRST_ROW_BOOL_VALUE = true
        private const val STATUS_SECOND_ROW_BOOL_VALUE = false

        private val TEXT_TYPE = ResultRow.Types("text", "varchar", "bpchar")

        @JvmStatic
        @Language("Postgresql")
        private val CREATE_TABLE = """
            | CREATE TABLE $TABLE_NAME (
            |    $ID_COLUMN_NAME TEXT NOT NULL,
            |    $STATUS_COLUMN_NAME BOOL NOT NULL,
            |    PRIMARY KEY ($ID_COLUMN_NAME)
            | );
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private val SELECT_SQL = """
            |   SELECT $ID_COLUMN_NAME,
            |          $STATUS_COLUMN_NAME
            |     FROM $TABLE_NAME
            | ORDER BY id
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private val INSERT_SQL = """
            | INSERT INTO $TABLE_NAME ($ID_COLUMN_NAME, $STATUS_COLUMN_NAME)
            |      VALUES ('$ID_FIRST_ROW_VALUE', '$STATUS_FIRST_ROW_BOOL_VALUE');
            | INSERT INTO $TABLE_NAME ($ID_COLUMN_NAME, $STATUS_COLUMN_NAME)
            |      VALUES ('$ID_SECOND_ROW_VALUE', '$STATUS_SECOND_ROW_BOOL_VALUE');
        """.trimMargin()
    }
}
