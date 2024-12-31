package io.github.ustudiocompany.uframework.jdbc.statement

import io.github.airflux.commons.types.resultk.matcher.shouldBeFailure
import io.github.ustudiocompany.uframework.jdbc.PostgresContainerTest
import io.github.ustudiocompany.uframework.jdbc.error.JDBCErrors
import io.github.ustudiocompany.uframework.jdbc.sql.ParametrizedSql
import io.github.ustudiocompany.uframework.test.kotest.IntegrationTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.intellij.lang.annotations.Language

internal class PreparedInsertStatementTest : IntegrationTest() {

    private val container = PostgresContainerTest()

    init {

        "The Custom error" - {
            container.executeSql(CREATE_TABLE)
            container.executeSql(CREATE_TRIGGER)

            "when the table with custom error trigger and data was inserted" - {
                val connection = container.dataSource.connection
                val result = connection.createPreparedInsertStatement(ParametrizedSql.of(INSERT_TEST_DATA)).execute()

                "then result should be JDBCErrors.Custom type with custom error code" {
                    result.shouldBeFailure()

                    val error = result.cause.shouldBeInstanceOf<JDBCErrors.Custom>()
                    error.state shouldBe CUSTOM_ERROR_CODE
                }
            }
        }
    }

    companion object {

        private const val TABLE_NAME = "test_custom_error_table"
        private const val CUSTOM_ERROR_CODE = "U0001"

        @JvmStatic
        @Language("Postgresql")
        private val CREATE_TABLE = """
            | CREATE TABLE $TABLE_NAME (
            |    id INTEGER PRIMARY KEY
            | );
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private val INSERT_TEST_DATA = """
            | INSERT INTO $TABLE_NAME (id) 
            |    VALUES (1);
        """.trimMargin()

        @JvmStatic
        @Language("Postgresql")
        private val CREATE_TRIGGER = """
            | CREATE OR REPLACE FUNCTION test_function()
            |     RETURNS TRIGGER AS ${'$'}${'$'}
            | BEGIN
            |     RAISE EXCEPTION 'Cannot insert'
            |         using ERRCODE = '$CUSTOM_ERROR_CODE';
            | END;
            | ${'$'}${'$'} LANGUAGE plpgsql;
            | CREATE TRIGGER test_trigger
            |     BEFORE INSERT ON $TABLE_NAME
            |     FOR EACH ROW
            |     EXECUTE FUNCTION test_function();
        """.trimMargin()
    }
}
