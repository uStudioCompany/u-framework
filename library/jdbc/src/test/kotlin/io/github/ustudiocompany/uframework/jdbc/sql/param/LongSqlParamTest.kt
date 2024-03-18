package io.github.ustudiocompany.uframework.jdbc.sql.param

import io.kotest.matchers.shouldBe

internal class LongSqlParamTest : AbstractSqlParamTest() {

    init {

        "The LongSqlParam type" - {
            executeSql(CREATE_TABLE)

            "when inserting a non-null value" - {
                truncateTable(TABLE_NAME)
                insertData(parametrizedSql, NON_NULLABLE_VALUE asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a passed value" {
                    checkData(SELECT_QUERY) {
                        getLong(VALUE_COLUMN_NAME) shouldBe NON_NULLABLE_VALUE
                    }
                }
            }

            "when inserting a null value" - {
                truncateTable(TABLE_NAME)
                insertData(parametrizedSql, NULLABLE_VALUE asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a null value" {
                    checkData(SELECT_QUERY) {
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
        private val parametrizedSql = parametrizedSql(TABLE_NAME)
    }
}
