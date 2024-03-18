package io.github.ustudiocompany.uframework.jdbc.sql.param

import io.kotest.matchers.shouldBe
import java.sql.Timestamp
import java.time.LocalDateTime

internal class TimestampSqlParamTest : AbstractSqlParamTest() {

    init {

        "The TimestampSqlParam type" - {
            executeSql(CREATE_TABLE)

            "when inserting a non-null value" - {
                truncateTable(TABLE_NAME)
                insertData(parametrizedSql, NON_NULLABLE_VALUE asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a passed value" {
                    checkData(SELECT_QUERY) {
                        getTimestamp(VALUE_COLUMN_NAME) shouldBe NON_NULLABLE_VALUE
                    }
                }
            }

            "when inserting a null value" - {
                truncateTable(TABLE_NAME)
                insertData(parametrizedSql, NULLABLE_VALUE asSqlParam VALUE_PARAM_NAME)

                "then a database should contain a null value" {
                    checkData(SELECT_QUERY) {
                        getTimestamp(1) shouldBe null
                        wasNull() shouldBe true
                    }
                }
            }
        }
    }

    private companion object {
        private const val TABLE_NAME = "test_timestamp_sql_param_table"
        private val NON_NULLABLE_VALUE: Timestamp = Timestamp.valueOf(LocalDateTime.now())
        private val NULLABLE_VALUE: Timestamp? = null

        @JvmStatic
        private val CREATE_TABLE = createTable(TABLE_NAME, "TIMESTAMP")

        @JvmStatic
        private val SELECT_QUERY = selectSql(TABLE_NAME)

        @JvmStatic
        private val parametrizedSql = parametrizedSql(TABLE_NAME)
    }
}
