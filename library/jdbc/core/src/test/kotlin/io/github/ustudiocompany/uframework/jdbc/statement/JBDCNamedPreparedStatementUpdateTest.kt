package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.liftToTransactionIncident
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeIncident
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.asSqlParam
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

internal class JBDCNamedPreparedStatementUpdateTest : IntegrationTest() {

    init {

        "The the `update` function of the JBDCNamedPreparedStatement type" - {
            val dataSource = install(postgresContainer())
            val tm: TransactionManager = transactionManager(dataSource = dataSource)
            dataSource.executeSql(CREATE_TABLE)

            "when the execution is successful" - {
                dataSource.truncateTable(TABLE_NAME)
                dataSource.executeSql(INSERT_SQL)
                val idParamName = "id"
                val titleParamName = "title"
                val result = tm.execute(UPDATE_SQL) { statement ->
                    statement.update(
                        ID_SECOND_ROW_VALUE.asSqlParam(idParamName),
                        TITLE_SECOND_ROW_NEW_VALUE.asSqlParam(titleParamName)
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
                    val idParamName = "id"
                    val result = tm.execute(UPDATE_SQL) { statement ->
                        statement.update(ID_SECOND_ROW_VALUE.asSqlParam(idParamName))
                    }

                    "then the result of execution of the statement should contain an incident" {
                        val error = result.shouldBeIncident()
                        error.description shouldBe "Error while executing the update."
                    }
                }

                "when parameter name is invalid" - {
                    dataSource.truncateTable(TABLE_NAME)
                    dataSource.executeSql(INSERT_SQL)
                    val idParamName = "id"
                    val invalidParamName = "title-invalid"
                    val result = tm.execute(UPDATE_SQL) { statement ->
                        statement.update(
                            ID_FIRST_ROW_VALUE.asSqlParam(idParamName),
                            TITLE_FIRST_ROW_VALUE.asSqlParam(invalidParamName)
                        )
                    }

                    "then the result of execution of the statement should contain an incident" {
                        val error = result.shouldBeIncident()
                        error.description shouldBe "Undefined parameter with name: '$invalidParamName'."
                    }
                }

                "when data was returned when none was expected " - {
                    dataSource.truncateTable(TABLE_NAME)
                    dataSource.executeSql(INSERT_SQL)
                    val idParamName = "id"
                    val result = tm.execute(SELECT_SQL) { statement ->
                        statement.update(ID_SECOND_ROW_VALUE.asSqlParam(idParamName))
                    }

                    "then the result of execution of the statement should contain an incident" {
                        val error = result.shouldBeIncident()
                        error.description shouldBe "Error while executing the update."
                    }
                }
            }
        }
    }

    private companion object {
        private const val TABLE_NAME = "test_table"

        private const val ID_COLUMN_NAME = "id"
        private const val TITLE_COLUMN_NAME = "title"

        private const val ID_PARAM_NAME = ":id"
        private const val TITLE_PARAM_NAME = ":title"

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
            |    WHERE $ID_COLUMN_NAME = $ID_PARAM_NAME
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private val UPDATE_SQL = """
            | UPDATE $TABLE_NAME
            |    SET $TITLE_COLUMN_NAME = $TITLE_PARAM_NAME
            |  WHERE $ID_COLUMN_NAME = $ID_PARAM_NAME
        """.trimMargin()

        private fun selectUpdated(id: String) = """
            | SELECT $TITLE_COLUMN_NAME
            |   FROM $TABLE_NAME
            | WHERE $ID_COLUMN_NAME = '$id'
        """.trimMargin()

        private fun <ValueT> TransactionManager.execute(
            sql: String,
            block: (statement: JBDCNamedPreparedStatement) -> JDBCResult<ValueT>
        ): TransactionResult<ValueT, Nothing> =
            useTransaction { connection ->
                connection.namedPreparedStatement(ParametrizedSql.of(sql))
                    .use { statement ->
                        block(statement).liftToTransactionIncident()
                    }
            }
    }
}
