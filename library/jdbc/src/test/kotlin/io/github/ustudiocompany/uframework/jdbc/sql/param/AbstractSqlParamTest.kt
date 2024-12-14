package io.github.ustudiocompany.uframework.jdbc.sql.param

import io.github.airflux.commons.types.resultk.matcher.shouldBeSuccess
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.jdbc.statement.createPreparedInsertStatement
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest

internal abstract class AbstractSqlParamTest : IntegrationTest() {

    protected val container = PostgresContainerTest()

    protected fun insertData(sql: ParametrizedSql, param: SqlParam) {
        container.dataSource
            .connection
            .use { connection ->
                val statement = connection.createPreparedInsertStatement(sql)
                val result = statement.execute(param)
                result shouldBeSuccess 1
            }
    }

    protected companion object {
        @JvmStatic
        val ID_COLUMN_NAME = "id"

        @JvmStatic
        val VALUE_COLUMN_NAME = "value"

        @JvmStatic
        val VALUE_PARAM_NAME = "value-param"

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

        fun parametrizedSql(tableName: String) = ParametrizedSql.of(
            """
            | INSERT INTO $tableName($ID_COLUMN_NAME, $VALUE_COLUMN_NAME)
            |      VALUES (1, :$VALUE_PARAM_NAME);
            """.trimMargin()
        )
    }
}
