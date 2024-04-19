package io.github.ustudiocompany.uframework.jdbc.sql.param

import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.postgresql.util.PGobject

internal class JsonbSqlParamTest : AbstractSqlParamTest() {

    init {

        "The JsonSqlParam type and table with json column" - {
            container.executeSql(CREATE_TABLE_WITH_JSON_COLUMN)

            "when inserting a non-null value" - {
                container.truncateTable(TABLE_WITH_JSON_COLUMN)
                insertData(INSERT_INTO_TABLE_WITH_JSON_COLUMN, NON_NULLABLE_VALUE jsonbAsSqlParam VALUE_PARAM_NAME)

                "then database should contain a json value with element in different order" {
                    container.checkData(SELECT_FROM_TABLE_WITH_JSON_COLUMN) {
                        val jsonObject = getObject(VALUE_COLUMN_NAME, PGobject::class.java)
                        jsonObject.type.equals(COLUMN_TYPE_JSON, true) shouldBe true
                        val jsonValue = jsonObject.value.shouldNotBeNull()
                        jsonValue.length shouldBe NON_NULLABLE_VALUE.length
                        jsonValue shouldNotBe NON_NULLABLE_VALUE
                        jsonValue.shouldBeEqualToComparingFields(NON_NULLABLE_VALUE)
                    }
                }
            }

            "when inserting a null value" - {
                container.truncateTable(TABLE_WITH_JSON_COLUMN)
                insertData(INSERT_INTO_TABLE_WITH_JSON_COLUMN, NULLABLE_VALUE jsonbAsSqlParam VALUE_PARAM_NAME)

                "then database should contain a null value" {
                    container.checkData(SELECT_FROM_TABLE_WITH_JSON_COLUMN) {
                        val jsonObject = getObject(VALUE_COLUMN_NAME, PGobject::class.java)
                        jsonObject.value shouldBe null
                        wasNull() shouldBe true
                    }
                }
            }
        }

        "The JsonSqlParam type and table with jsonb column" - {
            container.executeSql(CREATE_TABLE_WITH_JSONB_COLUMN)

            "when inserting a non-null value" - {
                container.truncateTable(TABLE_WITH_JSONB_COLUMN)
                insertData(INSERT_INTO_TABLE_WITH_JSONB_COLUMN, NON_NULLABLE_VALUE jsonbAsSqlParam VALUE_PARAM_NAME)

                "then database should contain a jsonb value with element in different order" {
                    container.checkData(SELECT_FROM_TABLE_WITH_JSONB_COLUMN) {
                        val jsonObject = getObject(VALUE_COLUMN_NAME, PGobject::class.java)
                        jsonObject.type.equals(COLUMN_TYPE_JSONB, true) shouldBe true
                        val jsonValue = jsonObject.value.shouldNotBeNull()
                        jsonValue.length shouldBe NON_NULLABLE_VALUE.length
                        jsonValue shouldNotBe NON_NULLABLE_VALUE
                        jsonValue.shouldBeEqualToComparingFields(NON_NULLABLE_VALUE)
                    }
                }
            }

            "when inserting a null value" - {
                container.truncateTable(TABLE_WITH_JSONB_COLUMN)
                insertData(INSERT_INTO_TABLE_WITH_JSONB_COLUMN, NULLABLE_VALUE jsonbAsSqlParam VALUE_PARAM_NAME)

                "then database should contain a null value" {
                    container.checkData(SELECT_FROM_TABLE_WITH_JSONB_COLUMN) {
                        val jsonObject = getObject(VALUE_COLUMN_NAME, PGobject::class.java)
                        jsonObject.value shouldBe null
                        wasNull() shouldBe true
                    }
                }
            }
        }
    }

    companion object {
        private const val TABLE_WITH_JSON_COLUMN = "test_json_sql_param_table"
        private const val TABLE_WITH_JSONB_COLUMN = "test_jsonb_sql_param_table"
        private const val COLUMN_TYPE_JSON = "json"
        private const val COLUMN_TYPE_JSONB = "jsonb"

        private val NON_NULLABLE_VALUE: String = """
            {"string": "string value", "number": 1, "boolean": true}
            """.trimIndent()
        private val NULLABLE_VALUE: String? = null

        @JvmStatic
        private val CREATE_TABLE_WITH_JSON_COLUMN = createTable(TABLE_WITH_JSON_COLUMN, COLUMN_TYPE_JSON)

        @JvmStatic
        private val CREATE_TABLE_WITH_JSONB_COLUMN = createTable(TABLE_WITH_JSONB_COLUMN, COLUMN_TYPE_JSONB)

        @JvmStatic
        private val INSERT_INTO_TABLE_WITH_JSON_COLUMN = parametrizedSql(TABLE_WITH_JSON_COLUMN)

        @JvmStatic
        private val INSERT_INTO_TABLE_WITH_JSONB_COLUMN = parametrizedSql(TABLE_WITH_JSONB_COLUMN)

        @JvmStatic
        private val SELECT_FROM_TABLE_WITH_JSON_COLUMN = selectSql(TABLE_WITH_JSON_COLUMN)

        @JvmStatic
        private val SELECT_FROM_TABLE_WITH_JSONB_COLUMN = selectSql(TABLE_WITH_JSONB_COLUMN)
    }
}
