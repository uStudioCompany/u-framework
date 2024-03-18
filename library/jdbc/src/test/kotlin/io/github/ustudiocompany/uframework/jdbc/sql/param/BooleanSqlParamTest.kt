package io.github.ustudiocompany.uframework.jdbc.sql.param

import io.kotest.matchers.shouldBe

internal class BooleanSqlParamTest : AbstractSqlParamTest() {

    init {

        "The LongSqlParam type" - {
            executeSql(CREATE_TABLE)

            "when inserting a non-null value" - {
                truncateTable(TABLE_NAME)
                val data = Data(VALUE_COLUMN_VALUE)
                insertData(parametrizedSql, data.value asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a passed value" {
                    checkData(SELECT_QUERY) {
                        getBoolean(VALUE_COLUMN_NAME) shouldBe VALUE_COLUMN_VALUE
                    }
                }
            }

            "when inserting a null value" - {
                truncateTable(TABLE_NAME)
                val data = Data(null)
                insertData(parametrizedSql, data.value asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a null value" {
                    checkData(SELECT_QUERY) {
                        getBoolean(1) shouldBe false
                        wasNull() shouldBe true
                    }
                }
            }
        }
    }

    private data class Data(val value: Boolean?)

    private companion object {
        private const val TABLE_NAME = "test_boolean_sql_param_table"
        private const val VALUE_COLUMN_VALUE = true

        @JvmStatic
        private val CREATE_TABLE = createTable(TABLE_NAME, "BOOLEAN")

        @JvmStatic
        private val SELECT_QUERY = selectSql(TABLE_NAME)

        @JvmStatic
        private val parametrizedSql = parametrizedSql(TABLE_NAME)
    }
}
