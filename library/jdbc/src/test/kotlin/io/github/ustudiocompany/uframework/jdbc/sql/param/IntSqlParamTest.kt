package io.github.ustudiocompany.uframework.jdbc.sql.param

import io.kotest.matchers.shouldBe

internal class IntSqlParamTest : AbstractSqlParamTest() {

    init {

        "The IntSqlParam type" - {
            container.executeSql(CREATE_TABLE)

            "when inserting a non-null value" - {
                container.truncateTable(TABLE_NAME)
                insertData(parametrizedSql, NON_NULLABLE_VALUE asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a passed value" {
                    container.checkData(SELECT_QUERY) {
                        getInt(VALUE_COLUMN_NAME) shouldBe NON_NULLABLE_VALUE
                    }
                }
            }

            "when inserting a null value" - {
                container.truncateTable(TABLE_NAME)
                insertData(parametrizedSql, NULLABLE_VALUE asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a null value" {
                    container.checkData(SELECT_QUERY) {
                        getInt(1) shouldBe 0
                        wasNull() shouldBe true
                    }
                }
            }
        }
    }

    private companion object {
        private const val TABLE_NAME = "test_int_sql_param_table"
        private const val NON_NULLABLE_VALUE: Int = Int.MAX_VALUE
        private val NULLABLE_VALUE: Int? = null

        @JvmStatic
        private val CREATE_TABLE = createTable(TABLE_NAME, "INTEGER")

        @JvmStatic
        private val SELECT_QUERY = selectSql(TABLE_NAME)

        @JvmStatic
        private val parametrizedSql = parametrizedSql(TABLE_NAME)
    }
}
