package io.github.ustudiocompany.uframework.jdbc.sql.param

import io.kotest.matchers.shouldBe

internal class IntSqlParamTest : AbstractSqlParamTest() {

    init {

        "The IntSqlParam type" - {
            executeSql(CREATE_TABLE)

            "when inserting a non-null value" - {
                truncateTable(TABLE_NAME)
                val data = Data(VALUE_COLUMN_VALUE)
                insertData(parametrizedSql, data.value asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a passed value" {
                    checkData(SELECT_QUERY) {
                        getInt(VALUE_COLUMN_NAME) shouldBe VALUE_COLUMN_VALUE
                    }
                }
            }

            "when inserting a null value" - {
                truncateTable(TABLE_NAME)
                val data = Data(null)
                insertData(parametrizedSql, data.value asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a null value" {
                    checkData(SELECT_QUERY) {
                        getInt(1) shouldBe 0
                        wasNull() shouldBe true
                    }
                }
            }
        }
    }

    private data class Data(val value: Int?)

    private companion object {
        private const val TABLE_NAME = "test_int_sql_param_table"
        private const val VALUE_COLUMN_VALUE = Int.MAX_VALUE

        @JvmStatic
        private val CREATE_TABLE = createTable(TABLE_NAME, "INTEGER")

        @JvmStatic
        private val SELECT_QUERY = selectSql(TABLE_NAME)

        @JvmStatic
        private val parametrizedSql = parametrizedSql(TABLE_NAME)
    }
}
