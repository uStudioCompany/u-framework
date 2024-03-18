package io.github.ustudiocompany.uframework.jdbc.sql.param

import io.kotest.matchers.shouldBe
import java.util.*

internal class UUIDSqlParamTest : AbstractSqlParamTest() {

    init {

        "The UUIDSqlParam type" - {
            executeSql(CREATE_TABLE)

            "when inserting a non-null value" - {
                truncateTable(TABLE_NAME)
                val data = Data(VALUE_COLUMN_VALUE)
                insertData(parametrizedSql, data.value asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a passed value" {
                    checkData(SELECT_QUERY) {
                        getObject(VALUE_COLUMN_NAME, UUID::class.java) shouldBe VALUE_COLUMN_VALUE
                    }
                }
            }

            "when inserting a null value" - {
                truncateTable(TABLE_NAME)
                val data = Data(null)
                insertData(parametrizedSql, data.value asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a null value" {
                    checkData(SELECT_QUERY) {
                        getObject(VALUE_COLUMN_NAME, UUID::class.java) shouldBe null
                        wasNull() shouldBe true
                    }
                }
            }
        }
    }

    private data class Data(val value: UUID?)

    private companion object {
        private const val TABLE_NAME = "test_string_sql_param_table"
        private val VALUE_COLUMN_VALUE: UUID = UUID.randomUUID()

        @JvmStatic
        private val CREATE_TABLE = createTable(TABLE_NAME, "UUID")

        @JvmStatic
        private val SELECT_QUERY = selectSql(TABLE_NAME)

        @JvmStatic
        private val parametrizedSql = parametrizedSql(TABLE_NAME)
    }
}
