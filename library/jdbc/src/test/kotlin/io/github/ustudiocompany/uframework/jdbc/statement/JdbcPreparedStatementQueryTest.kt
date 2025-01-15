package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.liftToIncident
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeIncident
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.row.extract
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.sqlParam
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
import io.github.ustudiocompany.uframework.jdbc.use
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

internal class JdbcPreparedStatementQueryTest : IntegrationTest() {

    init {

        "The the `query` function of the JdbcPreparedStatement type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(CREATE_TABLE)

            "when the execution is successful" - {
                container.truncateTable(TABLE_NAME)
                container.executeSql(INSERT_SQL)
                val result = tm.execute(SELECT_SQL) { statement ->
                    statement.query(sqlParam(ID_SECOND_ROW_VALUE))
                        .andThen { rows ->
                            rows.traverse { row ->
                                row.getString(TITLE_COLUMN_INDEX)
                            }
                        }
                }

                "then the result should contain only the selected data" {
                    result.shouldBeSuccess()
                    result.value.size shouldBe 1
                    result.value shouldContainExactly listOf(TITLE_SECOND_ROW_VALUE)
                }
            }

            "when the execution is failed" - {

                "when the parameter is not specified" - {
                    container.truncateTable(TABLE_NAME)
                    container.executeSql(INSERT_SQL)
                    val result = tm.execute(SELECT_SQL) { statement ->
                        statement.query()
                            .andThen { rows ->
                                rows.traverse { row ->
                                    row.getString(TITLE_COLUMN_INDEX)
                                }
                            }
                    }

                    "then the result of execution of the statement should contain an incident" {
                        val error = result.shouldBeIncident()
                        error.description shouldBe "Error while executing the query."
                    }
                }

                "when parameter out of range" - {
                    container.truncateTable(TABLE_NAME)
                    container.executeSql(INSERT_SQL)
                    val result = tm.execute(SELECT_SQL) { statement ->
                        statement.query(sqlParam(ID_FIRST_ROW_VALUE), sqlParam(ID_SECOND_ROW_VALUE))
                            .andThen { rows ->
                                rows.traverse { row ->
                                    row.getString(TITLE_COLUMN_INDEX)
                                }
                            }
                    }

                    "then the result of execution of the statement should contain an incident" {
                        val error = result.shouldBeIncident()
                        error.description shouldBe "Error while setting parameter by index: '2'."
                    }
                }

                "when query does not contain data" - {
                    container.truncateTable(TABLE_NAME)
                    container.executeSql(INSERT_SQL)
                    val result = tm.execute(UPDATE_SQL) { statement ->
                        statement.query(
                            sqlParam(TITLE_SECOND_ROW_NEW_VALUE),
                            sqlParam(ID_SECOND_ROW_VALUE)
                        ).andThen { rows ->
                            rows.traverse { row ->
                                row.getString(TITLE_COLUMN_INDEX)
                            }
                        }
                    }

                    "then the result of execution of the statement should contain an incident" {
                        val error = result.shouldBeIncident()
                        error.description shouldBe "Error while executing the query."
                    }
                }
            }
        }
    }

    private fun ResultRow.getString(column: Int) =
        extract(column, TEXT_TYPE) { col, rs -> rs.getString(col) }

    private companion object {
        private const val TABLE_NAME = "test_table"

        private const val ID_COLUMN_NAME = "id"
        private const val TITLE_COLUMN_NAME = "title"

        private const val ID_FIRST_ROW_VALUE = "f-r-id"
        private const val ID_SECOND_ROW_VALUE = "s-r-id"

        private const val TITLE_FIRST_ROW_VALUE = "f-r-title"
        private const val TITLE_SECOND_ROW_VALUE = "s-r-title"
        private const val TITLE_SECOND_ROW_NEW_VALUE = "s-r-title-new"

        private const val TITLE_COLUMN_INDEX = 2

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

        @JvmStatic
        @Language("Postgresql")
        private val UPDATE_SQL = """
            | UPDATE $TABLE_NAME
            |    SET $TITLE_COLUMN_NAME = ?
            |  WHERE $ID_COLUMN_NAME = ?
        """.trimMargin()

        private fun <T> TransactionManager.execute(
            sql: String,
            block: (statement: JdbcPreparedStatement) -> JDBCResult<T>
        ): TransactionResult<T, Nothing> =
            useTransaction { connection ->
                connection.preparedStatement(sql)
                    .use { statement ->
                        block(statement).liftToIncident()
                    }
            }
    }
}
