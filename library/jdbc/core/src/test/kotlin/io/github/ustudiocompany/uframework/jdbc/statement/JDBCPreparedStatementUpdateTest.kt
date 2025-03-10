package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.liftToException
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.error.JDBCError
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldContainExceptionInstance
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.sqlParam
import io.github.ustudiocompany.uframework.jdbc.test.executeSql
import io.github.ustudiocompany.uframework.jdbc.test.postgresContainer
import io.github.ustudiocompany.uframework.jdbc.test.shouldContainExactly
import io.github.ustudiocompany.uframework.jdbc.test.truncateTable
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
import io.github.ustudiocompany.uframework.jdbc.use
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import io.kotest.core.extensions.install
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

@OptIn(AirfluxTypesExperimental::class)
internal class JDBCPreparedStatementUpdateTest : IntegrationTest() {

    init {

        "The the `update` function of the JDBCPreparedStatement type" - {
            val dataSource = install(postgresContainer())
            val tm: TransactionManager = transactionManager(dataSource = dataSource)
            dataSource.executeSql(CREATE_TABLE)

            "when the execution is successful" - {
                dataSource.truncateTable(TABLE_NAME)
                dataSource.executeSql(INSERT_SQL)
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
                    dataSource.shouldContainExactly(selectUpdated(ID_SECOND_ROW_VALUE)) {
                        getString(1) shouldBe TITLE_SECOND_ROW_NEW_VALUE
                    }
                }
            }

            "when the execution is failed" - {

                "when the parameter is not specified" - {
                    dataSource.truncateTable(TABLE_NAME)
                    dataSource.executeSql(INSERT_SQL)
                    val result = tm.execute(UPDATE_SQL) { statement ->
                        statement.update(sqlParam(ID_SECOND_ROW_VALUE))
                    }

                    "then the result of execution of the statement should contain an exception" {
                        val exception = result.shouldContainExceptionInstance()
                        exception.description shouldBe "Error while executing the update."
                    }
                }

                "when parameter out of range" - {
                    dataSource.truncateTable(TABLE_NAME)
                    dataSource.executeSql(INSERT_SQL)
                    val result = tm.execute(UPDATE_SQL) { statement ->
                        statement.update(
                            sqlParam(ID_FIRST_ROW_VALUE),
                            sqlParam(ID_SECOND_ROW_VALUE),
                            sqlParam(TITLE_FIRST_ROW_VALUE)
                        )
                    }

                    "then the result of execution of the statement should contain an exception" {
                        val exception = result.shouldContainExceptionInstance()
                        exception.description shouldBe "Error while setting parameter by index: '3'."
                    }
                }

                "when data was returned when none was expected " - {
                    dataSource.truncateTable(TABLE_NAME)
                    dataSource.executeSql(INSERT_SQL)
                    val result = tm.execute(SELECT_SQL) { statement ->
                        statement.update(sqlParam(ID_SECOND_ROW_VALUE))
                    }

                    "then the result of execution of the statement should contain an exception" {
                        val exception = result.shouldContainExceptionInstance()
                        exception.description shouldBe "Error while executing the update."
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

        private fun <ValueT> TransactionManager.execute(
            sql: String,
            block: (statement: JDBCPreparedStatement) -> JDBCResult<ValueT>
        ): TransactionResult<ValueT, Nothing, JDBCError> =
            useTransaction { connection ->
                connection.preparedStatement(sql)
                    .use { statement ->
                        block(statement).liftToException()
                    }
            }
    }
}
