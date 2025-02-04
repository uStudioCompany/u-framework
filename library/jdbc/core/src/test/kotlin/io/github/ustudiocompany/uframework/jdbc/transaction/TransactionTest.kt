package io.github.ustudiocompany.uframework.jdbc.transaction

import io.github.airflux.commons.types.AirfluxTypesExperimental
import io.github.airflux.commons.types.resultk.ResultK
import io.github.airflux.commons.types.resultk.liftToException
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.airflux.commons.types.resultk.result
import io.github.airflux.commons.types.resultk.resultWith
import io.github.ustudiocompany.uframework.jdbc.matcher.shouldContainExceptionInstance
import io.github.ustudiocompany.uframework.jdbc.test.executeSql
import io.github.ustudiocompany.uframework.jdbc.test.postgresContainer
import io.github.ustudiocompany.uframework.jdbc.test.shouldBeEmpty
import io.github.ustudiocompany.uframework.jdbc.test.shouldContainExactly
import io.github.ustudiocompany.uframework.jdbc.test.truncateTable
import io.github.ustudiocompany.uframework.jdbc.use
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import io.kotest.core.extensions.install
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language

@OptIn(AirfluxTypesExperimental::class)
internal class TransactionTest : IntegrationTest() {

    init {

        "The `Transaction` type" - {
            val dataSource = install(postgresContainer())
            dataSource.executeSql(createTable(FIRST_TABLE_NAME))
            dataSource.executeSql(createTable(SECOND_TABLE_NAME))
            val tm: TransactionManager = transactionManager(dataSource = dataSource)

            "when operations of the transaction are successful" - {

                "when the transaction commit is not called" - {
                    dataSource.truncateTable(FIRST_TABLE_NAME)
                    dataSource.truncateTable(SECOND_TABLE_NAME)
                    val result = tm.startTransaction()
                        .use { transaction ->
                            resultWith {
                                transaction.connection
                                    .preparedStatement(insertDataSQL(FIRST_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                transaction.connection
                                    .preparedStatement(insertDataSQL(SECOND_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                ResultK.Success.asUnit
                            }
                        }

                    "then the result of the execution transaction should be successful" {
                        result.shouldBeSuccess()
                    }

                    "then the data should not be saved in first table" {
                        dataSource.shouldBeEmpty(selectDataSQL(FIRST_TABLE_NAME))
                    }

                    "then the data should not be saved in second table" {
                        dataSource.shouldBeEmpty(selectDataSQL(SECOND_TABLE_NAME))
                    }
                }

                "when the transaction commit is successful" - {
                    dataSource.truncateTable(FIRST_TABLE_NAME)
                    dataSource.truncateTable(SECOND_TABLE_NAME)
                    val result = tm.startTransaction()
                        .use { transaction ->
                            resultWith {
                                transaction.connection
                                    .preparedStatement(insertDataSQL(FIRST_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                transaction.connection
                                    .preparedStatement(insertDataSQL(SECOND_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                transaction.commit().liftToException()
                            }
                        }

                    "then the result of the execution transaction should be successful" {
                        result.shouldBeSuccess()
                    }

                    "then the data in first table should be saved" {
                        dataSource.shouldContainExactly(selectDataSQL(FIRST_TABLE_NAME)) {
                            getString(TITLE_COLUMN_INDEX) shouldBe TITLE_FIRST_ROW_VALUE
                        }
                    }

                    "then the data in second table should be saved" {
                        dataSource.shouldContainExactly(selectDataSQL(SECOND_TABLE_NAME)) {
                            getString(TITLE_COLUMN_INDEX)
                        }
                    }
                }

                "when the transaction commit was called multiple times" - {
                    dataSource.truncateTable(FIRST_TABLE_NAME)
                    dataSource.truncateTable(SECOND_TABLE_NAME)
                    val result = tm.startTransaction()
                        .use { transaction ->
                            resultWith {
                                transaction.connection
                                    .preparedStatement(insertDataSQL(FIRST_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()
                                transaction.commit().liftToException().raise()

                                transaction.connection
                                    .preparedStatement(insertDataSQL(SECOND_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                transaction.commit().liftToException()
                            }
                        }

                    "then the result of the execution transaction should be successful" {
                        result.shouldBeSuccess()
                    }

                    "then the data in first tables should be saved" {
                        dataSource.shouldContainExactly(selectDataSQL(FIRST_TABLE_NAME)) {
                            getString(TITLE_COLUMN_INDEX) shouldBe TITLE_FIRST_ROW_VALUE
                        }
                    }

                    "then the data in second tables should be saved" {
                        dataSource.shouldContainExactly(selectDataSQL(SECOND_TABLE_NAME)) {
                            getString(TITLE_COLUMN_INDEX)
                        }
                    }
                }

                "when the transaction commit was called after the rollback" - {
                    dataSource.truncateTable(FIRST_TABLE_NAME)
                    dataSource.truncateTable(SECOND_TABLE_NAME)
                    val result = tm.startTransaction()
                        .use { transaction ->
                            resultWith {
                                transaction.connection
                                    .preparedStatement(insertDataSQL(FIRST_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                transaction.connection
                                    .preparedStatement(insertDataSQL(SECOND_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                transaction.rollback().liftToException().raise()
                                transaction.commit().liftToException()
                            }
                        }

                    "then the result of the execution transaction should be successful" {
                        result.shouldBeSuccess()
                    }

                    "then the data should not be saved in first table" {
                        dataSource.shouldBeEmpty(selectDataSQL(FIRST_TABLE_NAME))
                    }

                    "then the data should not be saved in second table" {
                        dataSource.shouldBeEmpty(selectDataSQL(SECOND_TABLE_NAME))
                    }
                }

                "when the transaction rollback is successful" - {
                    dataSource.truncateTable(FIRST_TABLE_NAME)
                    dataSource.truncateTable(SECOND_TABLE_NAME)
                    val result = tm.startTransaction()
                        .use { transaction ->
                            resultWith {
                                transaction.connection
                                    .preparedStatement(insertDataSQL(FIRST_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                transaction.connection
                                    .preparedStatement(insertDataSQL(SECOND_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                transaction.rollback().liftToException()
                            }
                        }

                    "then the result of the execution transaction should be successful" {
                        result.shouldBeSuccess()
                    }

                    "then the data should not be saved in first table" {
                        dataSource.shouldBeEmpty(selectDataSQL(FIRST_TABLE_NAME))
                    }

                    "then the data should not be saved in second table" {
                        dataSource.shouldBeEmpty(selectDataSQL(SECOND_TABLE_NAME))
                    }
                }

                "when the transaction rollback was called after the commit" - {
                    dataSource.truncateTable(FIRST_TABLE_NAME)
                    dataSource.truncateTable(SECOND_TABLE_NAME)
                    val result = tm.startTransaction()
                        .use { transaction ->
                            resultWith {
                                transaction.connection
                                    .preparedStatement(insertDataSQL(FIRST_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                transaction.connection
                                    .preparedStatement(insertDataSQL(SECOND_TABLE_NAME))
                                    .use { statement ->
                                        statement.update().liftToException()
                                    }
                                    .bind()

                                transaction.commit().liftToException().raise()
                                transaction.rollback().liftToException()
                            }
                        }

                    "then the result of the execution transaction should be successful" {
                        result.shouldBeSuccess()
                    }

                    "then the data in first table should be saved" {
                        dataSource.shouldContainExactly(selectDataSQL(FIRST_TABLE_NAME)) {
                            getString(TITLE_COLUMN_INDEX) shouldBe TITLE_FIRST_ROW_VALUE
                        }
                    }

                    "then the data in second table should be saved" {
                        dataSource.shouldContainExactly(selectDataSQL(SECOND_TABLE_NAME)) {
                            getString(TITLE_COLUMN_INDEX)
                        }
                    }
                }
            }

            "when some operation of the transaction is failed due to a data uniqueness conflict" - {
                dataSource.truncateTable(FIRST_TABLE_NAME)
                dataSource.truncateTable(SECOND_TABLE_NAME)
                val result = tm.startTransaction()
                    .use { transaction ->
                        resultWith {
                            transaction.connection
                                .preparedStatement(insertDataSQL(FIRST_TABLE_NAME))
                                .use { statement ->
                                    statement.update().liftToException()
                                }
                                .bind()

                            transaction.connection
                                .preparedStatement(insertDataSQL(SECOND_TABLE_NAME))
                                .use { statement ->
                                    result {
                                        val r1 = statement.update().bind()
                                        val r2 = statement.update().bind()
                                        r1 + r2
                                    }.liftToException()
                                }
                                .bind()

                            transaction.commit().liftToException()
                        }
                    }

                "then the result of the execution transaction should be exception" {
                    result.shouldContainExceptionInstance()
                }

                "then the data in all tables should not be saved" {
                    dataSource.shouldBeEmpty(selectDataSQL(FIRST_TABLE_NAME))
                    dataSource.shouldBeEmpty(selectDataSQL(SECOND_TABLE_NAME))
                }
            }
        }
    }

    private companion object {
        private const val FIRST_TABLE_NAME = "first_test_table"
        private const val SECOND_TABLE_NAME = "second_test_table"

        private const val ID_COLUMN_NAME = "id"
        private const val TITLE_COLUMN_NAME = "title"

        private const val ID_FIRST_ROW_VALUE = "f-r-id"
        private const val TITLE_FIRST_ROW_VALUE = "f-r-title"

        private const val TITLE_COLUMN_INDEX = 1

        @JvmStatic
        @Language("Postgresql")
        private fun createTable(tableName: String) = """
            | CREATE TABLE $tableName (
            |    $ID_COLUMN_NAME    TEXT NOT NULL,
            |    $TITLE_COLUMN_NAME TEXT NOT NULL,
            |    PRIMARY KEY ($ID_COLUMN_NAME)
            | );
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private fun insertDataSQL(tableName: String) = """
            | INSERT INTO $tableName ($ID_COLUMN_NAME, $TITLE_COLUMN_NAME)
            | VALUES ('$ID_FIRST_ROW_VALUE', '$TITLE_FIRST_ROW_VALUE');
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private fun selectDataSQL(tableName: String) = """
            | SELECT $TITLE_COLUMN_NAME
            |   FROM $tableName
            | WHERE $ID_COLUMN_NAME = '$ID_FIRST_ROW_VALUE'
        """.trimMargin()
    }
}
