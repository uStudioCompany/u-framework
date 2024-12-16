package io.github.ustudiocompany.uframework.jdbc.sql.param

import io.kotest.matchers.shouldBe

internal class StringSqlParamTest : AbstractSqlParamTest() {

    init {

        "The StringSqlParam type" - {
            container.executeSql(CREATE_TABLE)

            "when inserting a non-null value" - {
                container.truncateTable(TABLE_NAME)
                insertData(PARAMETRIZED_SQL, NON_NULLABLE_VALUE asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a passed value" {
                    container.checkData(SELECT_QUERY) {
                        getString(VALUE_COLUMN_NAME) shouldBe NON_NULLABLE_VALUE
                    }
                }
            }

            "when inserting a null value" - {
                container.truncateTable(TABLE_NAME)
                insertData(PARAMETRIZED_SQL, NULLABLE_VALUE asSqlParam VALUE_PARAM_NAME)

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
        private val PARAMETRIZED_SQL = parametrizedSql(TABLE_NAME)
    }
}
