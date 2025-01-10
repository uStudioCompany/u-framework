package io.github.ustudiocompany.uframework.jdbc.sql.parameter

import io.github.airflux.commons.types.resultk.andThen
import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.transaction.TransactionManager
import io.github.ustudiocompany.uframework.jdbc.transaction.useTransaction
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest

internal abstract class AbstractSqlParameterTest : IntegrationTest() {

    protected fun TransactionManager.insertData(sql: String, param: SqlParameter) {
        val result = useTransaction {
            it.preparedStatement(sql)
                .andThen { statement ->
                    statement.setParameter(1, param)
                    statement.update()
                }
        }

        result shouldBeSuccess 1
    }

    protected companion object {
        @JvmStatic
        val ID_COLUMN_NAME = "id"

        @JvmStatic
        val VALUE_COLUMN_NAME = "value"

        @JvmStatic
        fun createTable(tableName: String, type: String) = """
            | CREATE TABLE $tableName (
            |    $ID_COLUMN_NAME    INTEGER PRIMARY KEY,
            |    $VALUE_COLUMN_NAME $type
            | );
        """.trimMargin()

        @JvmStatic
        fun selectSql(tableName: String) = """
            | SELECT $VALUE_COLUMN_NAME
            |   FROM $tableName
            |  WHERE $ID_COLUMN_NAME = 1;
        """.trimMargin()

        fun insertSql(tableName: String) = """
            | INSERT INTO $tableName($ID_COLUMN_NAME, $VALUE_COLUMN_NAME)
            |      VALUES (1, ?);
        """.trimMargin()
    }
}
