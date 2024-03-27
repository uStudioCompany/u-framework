package io.github.ustudiocompany.uframework.jdbc.sql.param

import io.kotest.matchers.shouldBe

internal class BooleanSqlParamTest : AbstractSqlParamTest() {

    init {

        "The LongSqlParam type" - {
            container.executeSql(CREATE_TABLE)

            "when inserting a non-null value" - {
                container.truncateTable(TABLE_NAME)
                insertData(parametrizedSql, NON_NULLABLE_VALUE asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a passed value" {
                    container.checkData(SELECT_QUERY) {
                        getBoolean(VALUE_COLUMN_NAME) shouldBe NON_NULLABLE_VALUE
                    }
                }
            }

            "when inserting a null value" - {
                container.truncateTable(TABLE_NAME)
                insertData(parametrizedSql, NULLABLE_VALUE asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a null value" {
                    container.checkData(SELECT_QUERY) {
                        getBoolean(1) shouldBe false
                        wasNull() shouldBe true
                    }
                }
            }
        }
    }

    private companion object {
        private const val TABLE_NAME = "test_boolean_sql_param_table"
        private const val NON_NULLABLE_VALUE: Boolean = true
        private val NULLABLE_VALUE: Boolean? = null

        @JvmStatic
        private val CREATE_TABLE = createTable(TABLE_NAME, "BOOLEAN")

        @JvmStatic
        private val SELECT_QUERY = selectSql(TABLE_NAME)

        @JvmStatic
        private val parametrizedSql = parametrizedSql(TABLE_NAME)
    }
}
