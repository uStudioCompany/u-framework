package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.asSuccess
import io.github.airflux.commons.types.resultk.map
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.traverse
import io.github.ustudiocompany.uframework.jdbc.JDBCResult
import io.github.ustudiocompany.uframework.jdbc.liftToTransactionException
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldContainExceptionInstance
import io.github.ustudiocompany.uframework.jdbc.row.ResultRow
import io.github.ustudiocompany.uframework.jdbc.sql.parameter.SqlParameterSetter
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
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

internal class JBDCPreparedStatementSetParametersTest : IntegrationTest() {

    init {

        "The `setParameter` function with `SqlParameterSetter` of the JBDCPreparedStatement type" - {
            val dataSource = install(postgresContainer())
            val tm: TransactionManager = transactionManager(dataSource = dataSource)
            dataSource.executeSql(CREATE_TABLE)

            "when the parameter is successfully set" - {

                "when using the `query` method" - {
                    dataSource.truncateTable(TABLE_NAME)
                    dataSource.executeSql(INSERT_SQL)
                    val result = tm.execute(SELECT_SQL) { statement ->
                        statement.setParameters {
                            set(1, ID_SECOND_ROW_VALUE, STRING_SETTER)
                        }.query()
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

                "when using the `update` method" - {
                    dataSource.truncateTable(TABLE_NAME)
                    dataSource.executeSql(INSERT_SQL)
                    val result = tm.execute(UPDATE_SQL) { statement ->
                        statement.setParameters {
                            set(1, TITLE_SECOND_ROW_NEW_VALUE, STRING_SETTER)
                            set(2, ID_SECOND_ROW_VALUE, STRING_SETTER)
                        }.update()
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

                "when using the `execute` method" - {

                    "when the SQL for execution is a query" - {
                        dataSource.truncateTable(TABLE_NAME)
                        dataSource.executeSql(INSERT_SQL)
                        val result = tm.execute(SELECT_SQL) { statement ->
                            statement.setParameters {
                                set(1, ID_SECOND_ROW_VALUE, STRING_SETTER)
                            }.execute()
                                .andThen { result ->
                                    (result as StatementResult.Rows).get
                                        .traverse { row ->
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

                    "when the SQL for execution is a update" - {
                        dataSource.truncateTable(TABLE_NAME)
                        dataSource.executeSql(INSERT_SQL)
                        val result = tm.execute(UPDATE_SQL) { statement ->
                            statement.setParameters {
                                set(1, TITLE_SECOND_ROW_NEW_VALUE, STRING_SETTER)
                                set(2, ID_SECOND_ROW_VALUE, STRING_SETTER)
                            }.execute()
                                .map { result -> (result as StatementResult.Count).get }
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
                }
            }

            "when the parameter is failed to set" - {

                "when parameter index is out of range" - {

                    "when the index less than min" - {
                        dataSource.truncateTable(TABLE_NAME)
                        dataSource.executeSql(INSERT_SQL)
                        val invalidParamIndex = 0

                        val result = tm.execute(SELECT_SQL) { statement ->
                            statement.setParameters {
                                set(invalidParamIndex, ID_SECOND_ROW_VALUE, STRING_SETTER)
                                set(1, TITLE_SECOND_ROW_VALUE, STRING_SETTER)
                            }.query()
                                .andThen { rows ->
                                    rows.traverse { row ->
                                        row.getString(TITLE_COLUMN_INDEX)
                                    }
                                }
                        }

                        "then should return an exception" {
                            val exception = result.shouldContainExceptionInstance()
                            exception.description shouldBe "Error while setting parameter by index: '$invalidParamIndex'."
                        }
                    }

                    "when the index greater than max" - {
                        dataSource.truncateTable(TABLE_NAME)
                        dataSource.executeSql(INSERT_SQL)
                        val invalidParamIndex = 2
                        val result = tm.execute(SELECT_SQL) { statement ->
                            statement.setParameters {
                                set(invalidParamIndex, ID_SECOND_ROW_VALUE, STRING_SETTER)
                                set(1, TITLE_SECOND_ROW_VALUE, STRING_SETTER)
                            }.query()
                                .andThen { rows ->
                                    rows.traverse { row ->
                                        row.getString(TITLE_COLUMN_INDEX)
                                    }
                                }
                        }

                        "then should return an exception" {
                            val exception = result.shouldContainExceptionInstance()
                            exception.description shouldBe "Error while setting parameter by index: '$invalidParamIndex'."
                        }
                    }
                }
            }
        }
    }

    private fun ResultRow.getString(column: Int) =
        extract(column, TEXT_TYPE) { col, rs -> rs.getString(col).asSuccess() }

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
        private val TEXT_TYPE = ResultRow.ColumnTypes("text", "varchar", "bpchar")

        private val STRING_SETTER: SqlParameterSetter<String> = { index, value ->
            setString(index, value)
        }

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
            block: (statement: JBDCPreparedStatement) -> JDBCResult<ValueT>
        ): TransactionResult<ValueT, Nothing> =
            useTransaction { connection ->
                connection.preparedStatement(sql)
                    .use { statement ->
                        block(statement).liftToTransactionException()
                    }
            }
    }
}
