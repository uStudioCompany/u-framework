package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.sqlParam
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.intellij.lang.annotations.Language

internal class JdbcPreparedStatementUpdateTest : IntegrationTest() {

    init {

        "The the `update` function of the JdbcPreparedStatement type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(CREATE_TABLE)

            "when the execution is successful" - {
                container.truncateTable(TABLE_NAME)
                container.executeSql(INSERT_SQL)
                val result = tm.execute(UPDATE_SQL) { statement ->
                    statement.update(
                        sqlParam(TITLE_SECOND_ROW_NEW_VALUE),
                        sqlParam(ID_SECOND_ROW_VALUE)
                    )
                }

                "then result should contain count of updated rows" {
                    result.shouldBeSuccess()
                    result.value shouldBe 1
                }

                "then the data should be updated" {
                    container.checkData(selectUpdated(ID_SECOND_ROW_VALUE)) {
                        getString(1) shouldBe TITLE_SECOND_ROW_NEW_VALUE
                    }
                }
            }

            "when the execution is failed" - {

                "when the parameter is not specified" - {
                    container.truncateTable(TABLE_NAME)
                    container.executeSql(INSERT_SQL)
                    val result = tm.execute(UPDATE_SQL) { statement ->
                        statement.update(sqlParam(ID_SECOND_ROW_VALUE))
                    }

                    "then the result of execution of the statement should contain error" {
                        result.shouldBeFailure()
                        result.cause.shouldBeInstanceOf<JDBCErrors.Statement.ParameterNotSpecified>()
                    }
                }

                "when data was returned when none was expected " - {
                    container.truncateTable(TABLE_NAME)
                    container.executeSql(INSERT_SQL)
                    val result = tm.execute(SELECT_SQL) { statement ->
                        statement.update(sqlParam(ID_SECOND_ROW_VALUE))
                    }

                    "then the result of execution of the statement should contain error" {
                        result.shouldBeFailure()
                        result.cause.shouldBeInstanceOf<JDBCErrors.Statement.UnexpectedResult>()
                    }
                }
            }
        }
    }

    private companion object {
        private const val TABLE_NAME = "test_table"

        private const val ID_COLUMN_NAME = "id"
        private const val TITLE_COLUMN_NAME = "title"

        private const val ID_FIRST_ROW_VALUE = "f-r-id"
        private const val ID_SECOND_ROW_VALUE = "s-r-id"

        private const val TITLE_FIRST_ROW_VALUE = "f-r-title"
        private const val TITLE_SECOND_ROW_VALUE = "s-r-title"
        private const val TITLE_SECOND_ROW_NEW_VALUE = "s-r-title-new"

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

        private fun selectUpdated(id: String) = """
            | SELECT $TITLE_COLUMN_NAME
            |   FROM $TABLE_NAME
            | WHERE $ID_COLUMN_NAME = '$id'
        """.trimMargin()

        private fun <T> TransactionManager.execute(
            sql: String,
            block: (statement: JdbcPreparedStatement) -> JDBCResult<T>
        ) = useTransaction { connection ->
            connection.preparedStatement(sql).andThen { statement -> block(statement) }
        }
    }
}
