package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.liftToTransactionResult
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldBeIncident
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.asSqlParam
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionResult
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
import io.github.ustudiocompany.uframework.jdbc.use
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

internal class JdbcNamedPreparedStatementExecuteTest : IntegrationTest() {

    init {

        "The the `execute` function of the JdbcPreparedStatement type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(CREATE_TABLE)

            "when the execution is successful" - {

                "when the SQL for execution is a query" - {
                    container.truncateTable(TABLE_NAME)
                    container.executeSql(INSERT_SQL)
                    val idParamName = "id"
                    val result = tm.executeSql(SELECT_SQL) { statement ->
                        statement.execute(ID_SECOND_ROW_VALUE.asSqlParam(idParamName))
                            .andThen { result ->
                                (result as StatementResult.Rows).get.traverse { row ->
                                    row.extract(TITLE_COLUMN_INDEX, TEXT_TYPE) { col, rs ->
                                        rs.getString(col)
                                    }
                                }
                            }
                    }

                    "then the result should contain only the selected data" {
                        result.shouldBeSuccess()
                        result.value.size shouldBe 1
                        result.value shouldContainExactly listOf(TITLE_SECOND_ROW_VALUE)
                    }
                }

                "when the SQL for execution is a update" - {
                    container.truncateTable(TABLE_NAME)
                    container.executeSql(INSERT_SQL)
                    val idParamName = "id"
                    val titleParamName = "title"
                    val result = tm.executeSql(UPDATE_SQL) { statement ->
                        statement.execute(
                            ID_SECOND_ROW_VALUE.asSqlParam(idParamName),
                            TITLE_SECOND_ROW_NEW_VALUE.asSqlParam(titleParamName)
                        ).map { result ->
                            (result as StatementResult.Count).get
                        }
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
            }

            "when the execution is failed" - {

                "when the SQL for execution is a query" - {

                    "when the parameter is not specified" - {
                        container.truncateTable(TABLE_NAME)
                        container.executeSql(INSERT_SQL)
                        val result = tm.executeSql(SELECT_SQL) { statement ->
                            statement.execute()
                                .andThen { result ->
                                    (result as StatementResult.Rows).get.traverse { row ->
                                        row.extract(TITLE_COLUMN_INDEX, TEXT_TYPE) { col, rs ->
                                            rs.getString(col)
                                        }
                                    }
                                }
                        }

                        "then the result of execution of the statement should contain an incident" {
                            val error = result.shouldBeIncident()
                            error.description shouldBe "Error while executing the statement."
                        }
                    }

                    "when parameter name is invalid" - {
                        container.truncateTable(TABLE_NAME)
                        container.executeSql(INSERT_SQL)
                        val titleParamName = "title"
                        val result = tm.executeSql(SELECT_SQL) { statement ->
                            statement.execute(TITLE_FIRST_ROW_VALUE.asSqlParam(titleParamName))
                                .andThen { result ->
                                    (result as StatementResult.Rows).get.traverse { row ->
                                        row.extract(TITLE_COLUMN_INDEX, TEXT_TYPE) { col, rs ->
                                            rs.getString(col)
                                        }
                                    }
                                }
                        }

                        "then the result of execution of the statement should contain an incident" {
                            val error = result.shouldBeIncident()
                            error.description shouldBe "Undefined parameter with name: '$titleParamName'."
                        }
                    }
                }

                "when the SQL for execution is a update" - {

                    "when the parameter is not specified" - {
                        container.truncateTable(TABLE_NAME)
                        container.executeSql(INSERT_SQL)
                        val result = tm.executeSql(UPDATE_SQL) { statement ->
                            statement.execute().map { result ->
                                (result as StatementResult.Count).get
                            }
                        }

                        "then the result of execution of the statement should contain an incident" {
                            val error = result.shouldBeIncident()
                            error.description shouldBe "Error while executing the statement."
                        }
                    }

                    "when parameter name is invalid" - {
                        container.truncateTable(TABLE_NAME)
                        container.executeSql(INSERT_SQL)
                        val idParamName = "id"
                        val invalidParamName = "title-invalid"
                        val result = tm.executeSql(UPDATE_SQL) { statement ->
                            statement.execute(
                                ID_FIRST_ROW_VALUE.asSqlParam(idParamName),
                                TITLE_FIRST_ROW_VALUE.asSqlParam(invalidParamName)
                            ).map { result ->
                                (result as StatementResult.Count).get
                            }
                        }

                        "then the result of execution of the statement should contain an incident" {
                            val error = result.shouldBeIncident()
                            error.description shouldBe "Undefined parameter with name: '$invalidParamName'."
                        }
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

        private fun <ValueT> TransactionManager.executeSql(
            sql: String,
            block: (statement: JdbcNamedPreparedStatement) -> JDBCResult<ValueT>
        ): TransactionResult<ValueT, Nothing> =
            useTransaction { connection ->
                connection.namedPreparedStatement(ParametrizedSql.of(sql))
                    .use { statement ->
                        block(statement).liftToTransactionResult()
                    }
            }
    }
}
