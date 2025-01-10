package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.matchers.shouldBe

internal class StringSqlParameterTest : AbstractSqlParameterTest() {

    init {

        "The StringSqlParameter type" - {
            val container = PostgresContainerTest()
            val tm: TransactionManager = transactionManager(dataSource = container.dataSource)
            container.executeSql(CREATE_TABLE)

            "when inserting a non-null value" - {
                container.truncateTable(TABLE_NAME)
                tm.insertData(INSERT_SQL, NON_NULLABLE_VALUE.asSqlParam())

                "then a database should contain a passed value" {
                    container.checkData(SELECT_QUERY) {
                        getString(VALUE_COLUMN_NAME) shouldBe NON_NULLABLE_VALUE
                    }
                }
            }

            "when inserting a null value" - {
                container.truncateTable(TABLE_NAME)
                tm.insertData(INSERT_SQL, NULLABLE_VALUE.asSqlParam())

                "then a database should contain a null value" {
                    container.checkData(SELECT_QUERY) {
                        getString(1) shouldBe null
                        wasNull() shouldBe true
                    }
                }
            }
        }
    }

    private companion object {
        private const val TABLE_NAME = "test_string_sql_param_table"
        private const val NON_NULLABLE_VALUE: String = "STRING"
        private val NULLABLE_VALUE: String? = null

        @JvmStatic
        private val CREATE_TABLE = createTable(TABLE_NAME, "TEXT")

        @JvmStatic
        private val SELECT_QUERY = selectSql(TABLE_NAME)

        @JvmStatic
        private val INSERT_SQL = insertSql(TABLE_NAME)
    }
}
