package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import io.github.ustudiocompany.uframework.jdbc.test.checkData
import io.github.ustudiocompany.uframework.jdbc.test.executeSql
import io.github.ustudiocompany.uframework.jdbc.test.postgresContainer
import io.github.ustudiocompany.uframework.jdbc.test.truncateTable
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.transactionManager
import io.kotest.core.extensions.install
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.postgresql.util.PGobject

internal class JsonbSqlParameterTest : AbstractSqlParameterTest() {

    init {

        "The IntSqlParameter type" - {
            val dataSource = install(postgresContainer())
            val tm: TransactionManager = transactionManager(dataSource = dataSource)
            dataSource.executeSql(CREATE_TABLE_WITH_JSON_COLUMN)

            "a table with json column" - {

                "when inserting a non-null value" - {
                    dataSource.truncateTable(TABLE_WITH_JSON_COLUMN)
                    tm.insertData(INSERT_INTO_TABLE_WITH_JSON_COLUMN, NON_NULLABLE_VALUE.asJSONBSqlParam())

                    "then a database should contain the passed json" {
                        dataSource.checkData(SELECT_FROM_TABLE_WITH_JSON_COLUMN) {
                            val jsonObject: PGobject = getObject(VALUE_COLUMN_NAME, PGobject::class.java)
                            jsonObject.type.equals(COLUMN_TYPE_JSON, true) shouldBe true
                            val jsonValue = jsonObject.value.shouldNotBeNull()
                            jsonValue.shouldBeEqualToComparingFields(NON_NULLABLE_VALUE)
                        }
                    }
                }

                "when inserting a null value" - {
                    dataSource.truncateTable(TABLE_WITH_JSON_COLUMN)
                    tm.insertData(INSERT_INTO_TABLE_WITH_JSON_COLUMN, NULLABLE_VALUE.asJSONBSqlParam())

                    "then database should contain a null value" {
                        dataSource.checkData(SELECT_FROM_TABLE_WITH_JSON_COLUMN) {
                            val jsonObject = getObject(VALUE_COLUMN_NAME, PGobject::class.java)
                            jsonObject.value shouldBe null
                            wasNull() shouldBe true
                        }
                    }
                }
            }

            "a table with jsonb column" - {
                dataSource.executeSql(CREATE_TABLE_WITH_JSONB_COLUMN)

                "when inserting a non-null value" - {
                    dataSource.truncateTable(TABLE_WITH_JSONB_COLUMN)
                    tm.insertData(INSERT_INTO_TABLE_WITH_JSONB_COLUMN, NON_NULLABLE_VALUE.asJSONBSqlParam())

                    "then a database should contain the passed jsonb" {
                        dataSource.checkData(SELECT_FROM_TABLE_WITH_JSONB_COLUMN) {
                            val jsonObject = getObject(VALUE_COLUMN_NAME, PGobject::class.java)
                            jsonObject.type.equals(COLUMN_TYPE_JSONB, true) shouldBe true
                            val jsonValue = jsonObject.value.shouldNotBeNull()
                            jsonValue.shouldBeEqualToComparingFields(NON_NULLABLE_VALUE)
                        }
                    }
                }

                "when inserting a null value" - {
                    dataSource.truncateTable(TABLE_WITH_JSONB_COLUMN)
                    tm.insertData(INSERT_INTO_TABLE_WITH_JSONB_COLUMN, NULLABLE_VALUE.asJSONBSqlParam())

                    "then database should contain a null value" {
                        dataSource.checkData(SELECT_FROM_TABLE_WITH_JSONB_COLUMN) {
                            val jsonObject = getObject(VALUE_COLUMN_NAME, PGobject::class.java)
                            jsonObject.value shouldBe null
                            wasNull() shouldBe true
                        }
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
        private val INSERT_INTO_TABLE_WITH_JSON_COLUMN = insertSql(TABLE_WITH_JSON_COLUMN)

        @JvmStatic
        private val INSERT_INTO_TABLE_WITH_JSONB_COLUMN = insertSql(TABLE_WITH_JSONB_COLUMN)

        @JvmStatic
        private val SELECT_FROM_TABLE_WITH_JSON_COLUMN = selectSql(TABLE_WITH_JSON_COLUMN)

        @JvmStatic
        private val SELECT_FROM_TABLE_WITH_JSONB_COLUMN = selectSql(TABLE_WITH_JSONB_COLUMN)
    }
}
