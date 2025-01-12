package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.error.TransactionError
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.extract
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.sqlParam
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.intellij.lang.annotations.Language

internal class QueryForObjectTest : IntegrationTest() {

    init {

        "The `queryForObject` function of the JdbcPreparedStatement type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(CREATE_TABLE)

            "when the execution is successful" - {

                "when the query returns no data" - {
                    container.truncateTable(TABLE_NAME)
                    container.executeSql(INSERT_SQL)
                    val result = tm.execute(SELECT_SQL) { statement ->
                        statement.queryForObject(sqlParam(ID_THIRD_ROW_VALUE)) { index, row ->
                            row.setString(TITLE_COLUMN_INDEX)
                                .map { value -> index to value }
                        }
                    }

                    "then the result should be null" {
                        result.shouldBeSuccess()
                        result.value shouldBe null
                    }
                }

                "when the query returns data" - {
                    container.truncateTable(TABLE_NAME)
                    container.executeSql(INSERT_SQL)
                    val result = tm.execute(SELECT_SQL) { statement ->
                        statement.queryForObject(sqlParam(ID_SECOND_ROW_VALUE)) { index, row ->
                            row.setString(TITLE_COLUMN_INDEX)
                                .map { value -> index to value }
                        }
                    }

                    "then the result should contain only the selected data" {
                        result.shouldBeSuccess()
                        result.value shouldBe (ROW_INDEX to TITLE_SECOND_ROW_VALUE)
                    }
                }
            }

            "when the execution is failed" - {

                "when the parameter is not specified" - {
                    container.truncateTable(TABLE_NAME)
                    container.executeSql(INSERT_SQL)
                    val result: JDBCResult<String?> = tm.execute(SELECT_SQL) { statement ->
                        statement.queryForObject<String>(sqlParam(ID_FIRST_ROW_VALUE)) { index, row ->
                            error("Error")
                        }
                    }

                    "then the result should be null" {
                        result.shouldBeFailure()
                        result.cause.shouldBeInstanceOf<TransactionError.Unexpected>()
                    }
                }
            }
        }
    }

    private fun ResultRow.setString(column: Int) =
        extract(column, TEXT_TYPE) { col, rs -> rs.getString(col) }

    private companion object {
        private const val TABLE_NAME = "test_table"

        private const val ID_COLUMN_NAME = "id"
        private const val TITLE_COLUMN_NAME = "title"

        private const val ID_FIRST_ROW_VALUE = "f-r-id"
        private const val ID_SECOND_ROW_VALUE = "s-r-id"
        private const val ID_THIRD_ROW_VALUE = "t-r-id"

        private const val TITLE_FIRST_ROW_VALUE = "f-r-title"
        private const val TITLE_SECOND_ROW_VALUE = "s-r-title"

        private const val TITLE_COLUMN_INDEX = 2
        private const val ROW_INDEX = 1

        private val TEXT_TYPE = ResultRow.Types("text", "varchar", "bpchar")

        @JvmStatic
        @Language("Postgresql")
        private val CREATE_TABLE = """
            | CREATE TABLE $TABLE_NAME (
            |    $ID_COLUMN_NAME    TEXT NOT NULL,
            |    $TITLE_COLUMN_NAME TEXT NOT NULL,
            |    PRIMARY KEY ($ID_COLUMN_NAME)
            | );
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private val INSERT_SQL = """
            | INSERT INTO $TABLE_NAME ($ID_COLUMN_NAME, $TITLE_COLUMN_NAME)
            | VALUES ('$ID_FIRST_ROW_VALUE', '$TITLE_FIRST_ROW_VALUE');
            | INSERT INTO $TABLE_NAME ($ID_COLUMN_NAME, $TITLE_COLUMN_NAME)
            | VALUES ('$ID_SECOND_ROW_VALUE', '$TITLE_SECOND_ROW_VALUE');
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private val SELECT_SQL = """
            |   SELECT $ID_COLUMN_NAME,
            |          $TITLE_COLUMN_NAME
            |     FROM $TABLE_NAME
            |    WHERE $ID_COLUMN_NAME = ?
        """.trimMargin()

        private fun <T> TransactionManager.execute(
            sql: String,
            block: (statement: JdbcPreparedStatement) -> JDBCResult<T>
        ) = useTransaction { connection ->
            connection.preparedStatement(sql).andThen { statement -> block(statement) }
        }
    }
}
