package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import io.github.ustudiocompany.uframework.jdbc.test.executeSql
import io.github.ustudiocompany.uframework.jdbc.test.postgresContainer
import io.github.ustudiocompany.uframework.jdbc.test.shouldContainExactly
import io.github.ustudiocompany.uframework.jdbc.test.truncateTable
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.core.extensions.install
import io.kotest.matchers.shouldBe

internal class LongSqlParameterTest : AbstractSqlParameterTest() {

    init {

        "The LongSqlParameter type" - {
            val dataSource = install(postgresContainer())
            val tm: TransactionManager = transactionManager(dataSource = dataSource)
            dataSource.executeSql(CREATE_TABLE)

            "when inserting a non-null value" - {
                dataSource.truncateTable(TABLE_NAME)
                tm.insertData(INSERT_SQL, NON_NULLABLE_VALUE.asSqlParam())

                "then a database should contain a passed value" {
                    dataSource.shouldContainExactly(SELECT_QUERY) {
                        getLong(VALUE_COLUMN_NAME) shouldBe NON_NULLABLE_VALUE
                    }
                }
            }

            "when inserting a null value" - {
                dataSource.truncateTable(TABLE_NAME)
                tm.insertData(INSERT_SQL, NULLABLE_VALUE.asSqlParam())

                "then a database should contain a null value" {
                    dataSource.shouldContainExactly(SELECT_QUERY) {
                        getLong(1) shouldBe 0
                        wasNull() shouldBe true
                    }
                }
            }
        }
    }

    private companion object {
        private const val TABLE_NAME = "test_long_sql_param_table"
        private const val NON_NULLABLE_VALUE: Long = Long.MAX_VALUE
        private val NULLABLE_VALUE: Long? = null

        @JvmStatic
        private val CREATE_TABLE = createTable(TABLE_NAME, "BIGINT")

        @JvmStatic
        private val SELECT_QUERY = selectSql(TABLE_NAME)

        @JvmStatic
        private val INSERT_SQL = insertSql(TABLE_NAME)
    }
}
